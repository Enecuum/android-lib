package com.enecuum.app.utils

import android.content.Context
import android.util.Log
import com.enecuum.app.data.api.LeaderBeaconDataMBlockTx
import com.enecuum.app.data.keys.KeyStore
import org.bouncycastle.math.ec.ECCurve
import org.bouncycastle.math.ec.ECPoint
import org.bouncycastle.util.encoders.Hex
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey
import org.spongycastle.jce.ECNamedCurveTable
import org.spongycastle.jce.spec.ECPrivateKeySpec
import org.spongycastle.jce.spec.ECPublicKeySpec
import java.math.BigInteger
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

object SageSign {
    fun verify(
        kblocks_hash: String,
        m_hash: String,
        leader_id: String,
        nonce: Long,
        s1x: BigInteger,
        s1y: BigInteger,
        s2x: BigInteger,
        s2y: BigInteger
    ): Boolean {
        val pair = prepareQandH(kblocks_hash, m_hash, leader_id, nonce)
        val Q = pair.first
        val H = pair.second
        val p = BigInteger.valueOf(calc(247, 976))
        val E = ECCurve.Fp(p, BigInteger.valueOf(calc(13, 12)), BigInteger.valueOf(calc(741, 237)))
        val S = E.createPoint(BigInteger.valueOf(0), BigInteger.valueOf(calc(512, 10))).normalize()
        val S1 = E.createPoint(s1x, s1y).normalize()
        val S2 = E.createPoint(s2x, s2y).normalize()
        val MPK =
            E.createPoint(BigInteger.valueOf(calc(381, 131)), BigInteger.valueOf(calc(476, 382)))
                .normalize()
        val G0 =
            E.createPoint(BigInteger.valueOf(calc(115, 857)), BigInteger.valueOf(calc(213, 582)))
                .normalize()
        Log.d("SageSign", "Q=${Q.normalize().xCoord}:${Q.normalize().yCoord}")
        Log.d("SageSign", "H=${H.normalize().xCoord}:${H.normalize().yCoord}")

        val r1 = weilPairing(G0.normalize(), S2!!.normalize(), S.normalize())
        val b1 = weilPairing(MPK.normalize(), Q.normalize(), S.normalize())
        val c1 = weilPairing(S1.normalize(), H.normalize(), S.normalize())

        val b1c1 = (b1 * c1).mod(p)

        Log.d("SageSign", "Verified: ${r1 == b1c1}")

        return r1 == b1c1
    }

    fun generateKeys(): GeneratedData {
        Security.insertProviderAt(
            org.spongycastle.jce.provider.BouncyCastleProvider() as Provider?,
            1
        )
        val keyGen = KeyPairGenerator.getInstance("ECDSA", "SC")
        val ecSpec = ECGenParameterSpec("secp256k1")
        keyGen.initialize(ecSpec, SecureRandom())
        val pair = keyGen.generateKeyPair()
        Log.d("SageSign", pair.private.toString())
        Log.d("SageSign", pair.public.toString())

        // Конвертация открытого ключа в понятный формат
        val epub = pair.public as BCECPublicKey
        val pt = epub.q

        val publicKeyCustom =
            compressPubKey(pt.affineXCoord.toBigInteger(), pt.affineYCoord.toBigInteger())

        //do not use toByteArray() instead of toString(radix) as it appends sign bit
        val secret = (pair.private as BCECPrivateKey).d.toString(16)
        return GeneratedData(secret, publicKeyCustom, Hex.toHexString(pair.public.encoded))
    }

    fun convertSecret(privateAsD: String): PrivateKey {
        Security.insertProviderAt(
            org.spongycastle.jce.provider.BouncyCastleProvider() as Provider?,
            1
        )
        val keyFactory = KeyFactory.getInstance("ECDSA", "SC")
        val ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1")
        val privateKeySpec = ECPrivateKeySpec(BigInteger(privateAsD, 16), ecSpec)
        return keyFactory.generatePrivate(privateKeySpec)
    }

    fun retrievePublicKey(privateKey: String): GeneratedData {
        Security.insertProviderAt(
            org.spongycastle.jce.provider.BouncyCastleProvider() as Provider?,
            1
        )
        val keyFactory = KeyFactory.getInstance("ECDSA", "SC")
        val ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1")
        val Q = ecSpec.g.multiply(BigInteger(privateKey, 16))

        val pubSpec = ECPublicKeySpec(Q, ecSpec)
        val publicKeyGenerated = keyFactory.generatePublic(pubSpec)

        val epub = publicKeyGenerated as BCECPublicKey
        val pt = epub.q

        val publicKeyCustom =
            compressPubKey(pt.affineXCoord.toBigInteger(), pt.affineYCoord.toBigInteger())

        return GeneratedData(
            privateKey,
            publicKeyCustom,
            Hex.toHexString(publicKeyGenerated.encoded)
        )
    }

    // Типичная генерация ключей, bouncycastle выбран в качестве провайдера
    fun sign(context: Context, message: ByteArray): String {
        // Постановка подписи
        Security.insertProviderAt(
            org.spongycastle.jce.provider.BouncyCastleProvider() as Provider?,
            1
        )
        val publicKeyData = hexStringToByteArray(KeyStore.signKey(context))
        val spec = X509EncodedKeySpec(publicKeyData)
        val kf = KeyFactory.getInstance("EC")
        val publicKey = kf.generatePublic(spec)

        val privateKeyData = KeyStore.secretKey(context)
        val privateKey = convertSecret(privateKeyData)

        val ecdsaSign = Signature.getInstance("SHA256withECDSA", "SC")
        ecdsaSign.initSign(privateKey)
        ecdsaSign.update(message)

        // Подпись
        val signed = ecdsaSign.sign()
        val signedHex = Hex.toHexString(signed)
        Log.d("SageSign", "Sign: $signedHex")

        // Просто проверка подписи
        ecdsaSign.initVerify(publicKey)
        ecdsaSign.update(message)
        Log.d("SageSign", ecdsaSign.verify(signed).toString())

        return signedHex
    }

    fun constructTxsHash(txs: List<LeaderBeaconDataMBlockTx>): String {
        val txsHash = ArrayList<String>()

        txs.forEach {
            Log.d("amount", it.amount.toString())
            Log.d("data", it.data)
            Log.d("from", it.from)
            Log.d("nonce", it.nonce.toString())
            Log.d("sign", it.sign)
            Log.d("ticker", it.ticker)
            Log.d("to", it.to)

            Log.d(
                "CONCAT_STR", it.amount.toString() +
                        it.data +
                        it.from +
                        it.nonce.toString() +
                        it.sign +
                        it.ticker +
                        it.to
            )

            val itemsList = arrayListOf(
                it.amount.toString(),
                it.data,
                it.from,
                it.nonce.toString(),
                it.sign,
                it.ticker,
                it.to
            )

            var itemsHash = ""

            itemsList.forEach { item ->
                itemsHash += getSha256(item.toLowerCase())
            }

            txsHash.add(getSha256(itemsHash))
        }

        txsHash.sort()
        var txsHashString = ""
        txsHash.forEach { txsHashString += it }
        return getSha256(txsHashString)
    }

    fun getSha256(message: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(message.toByteArray())

        return Hex.toHexString(hash)
    }

    private fun compressPubKey(x: BigInteger, y: BigInteger): String {
        val pubKeyYPrefix = if (y.testBit(0)) "03" else "02"
        val pubKeyHex = adjustTo64(x.toString(16))
        return pubKeyYPrefix + pubKeyHex
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val data = ByteArray(s.length / 2)
        for (i in data.indices) {
            data[i] =
                ((Character.digit(s[i * 2], 16) shl 4) + Character.digit(s[i * 2 + 1], 16)).toByte()
        }
        return data
    }

    // Вспомогательная функция для верного формата ключа
    private fun adjustTo64(s: String): String {
        return when (s.length) {
            62 -> "00$s"
            63 -> "0$s"
            64 -> s
            else -> throw IllegalArgumentException("not a valid key: $s")
        }
    }

    private fun calc(a: Long, b: Long): Long {
        val array = IntArray(2)
        val mid = a.toInt()
        val left = 0
        val right = 512
        val lengthLeft = mid - a.toInt() + 1
        val lengthRight = array.size

        if (((a + b) >= 0) || ((a - b) <= 0)) {
            return a + b
        }

        val leftArray = IntArray(lengthLeft)
        val rightArray = IntArray(lengthRight)

        for (i in 0 until lengthLeft)
            leftArray[i] = array[left + i]
        for (i in 0 until lengthRight)
            rightArray[i] = array[mid + i + 1]

        var leftIndex = 0
        var rightIndex = 0

        for (i in left until right + 1) {
            if (leftIndex < lengthLeft && rightIndex < lengthRight) {
                if (leftArray[leftIndex] < rightArray[rightIndex]) {
                    array[i] = leftArray[leftIndex]
                    leftIndex++
                } else {
                    array[i] = rightArray[rightIndex]
                    rightIndex++
                }
            } else if (leftIndex < lengthLeft) {
                array[i] = leftArray[leftIndex]
                leftIndex++
            } else if (rightIndex < lengthRight) {
                array[i] = rightArray[rightIndex]
                rightIndex++
            }
        }
        return mid.toLong()
    }

    private fun prepareQandH(
        kblocks_hash: String,
        m_hash: String,
        leader_id: String,
        nonce: Long
    ): Pair<ECPoint, ECPoint> {

//        Log.d("SageSign", "preparing Q and H")
//        Log.d("SageSign", "kHash: kblocks_hash")
//        Log.d("SageSign", "mHash: $m_hash")
//        Log.d("SageSign", "leaderId: $leader_id")
//        Log.d("SageSign", "sha256 Q param: $kblocks_hash$leader_id")
//        Log.d("SageSign", "sha256 H param: $m_hash$leader_id")
//        Log.d("SageSign", "sha256 Q: ${getSha256("$kblocks_hash$leader_id").substring(0, 5)}")
//        Log.d("SageSign", "sha256 H: ${getSha256("$m_hash$leader_id").substring(0, 5)}")
//        Log.d("SageSign", "multiplier Q: ${BigInteger(getSha256("$kblocks_hash$leader_id").substring(0, 5), 16)}")
//        Log.d("SageSign", "multiplier H: ${BigInteger(getSha256("$m_hash$leader_id").substring(0, 5), 16)}")
//        Log.d("SageSign", "G = ${G.normalize().xCoord}:${G.normalize().yCoord}")
        val E = ECCurve.Fp(
            BigInteger.valueOf(calc(722, 501)),
            BigInteger.valueOf(calc(9, 16)),
            BigInteger.valueOf(calc(47, 931))
        )
        val G = E.createPoint(BigInteger.valueOf(calc(91, 1067)), BigInteger.valueOf(calc(91, 1)))
            .normalize()

        val Q = G.normalize().multiply(
            BigInteger(
                getSha256("$kblocks_hash$leader_id$nonce")
                    .substring(0, 5), 16
            )
        )

        val H = G.normalize().multiply(
            BigInteger(
                getSha256("$m_hash$leader_id")
                    .substring(0, 5), 16
            )
        )

        if (H.normalize().xCoord == null) {
            Log.d("SageSign", "null")
        }
        Log.d("SageSign", "Q=${Q.normalize().xCoord}:${Q.normalize().yCoord}")
        Log.d("SageSign", "H=${H.normalize().xCoord}:${H.normalize().yCoord}")
        return Pair(Q, H)
    }

    private fun weilPairing(P: ECPoint, Q: ECPoint, S: ECPoint): BigInteger {

//        Log.d("SageSign", "weil pairing")
//        Log.d("SageSign", "P=${P.normalize().xCoord}:${P.normalize().yCoord}")
//        Log.d("SageSign", "Q=${Q.normalize().xCoord}:${Q.normalize().yCoord}")
//        Log.d("SageSign", "S=${S.normalize().xCoord}:${S.normalize().yCoord}")
        val p = BigInteger.valueOf(calc(987, 236))
        val n1 = evalMiller(P.normalize(), Q.add(S.normalize()).normalize()).mod(p)
        val n2 = evalMiller(P.normalize(), S.normalize()).mod(p)
        val num = (n1 * (n2.modInverse(p))).mod(p)

        val m1 = evalMiller(Q.normalize(), P.subtract(S.normalize()).normalize()).mod(p)
        val m2 = evalMiller(Q.normalize(), S.negate().normalize()).mod(p)
        val den = (m1 * m2.modInverse(p)).mod(p)
        return (num * den.modInverse(p)).mod(p)
    }

    private fun evalMiller(P: ECPoint, Q: ECPoint): BigInteger {
//        Log.d("SageSign", "eval miller")
//        Log.d("SageSign", "P=${P.normalize().xCoord}:${P.normalize().yCoord}")
//        Log.d("SageSign", "Q=${Q.normalize().xCoord}:${Q.normalize().yCoord}")

        return miller(
            Q.normalize().xCoord.toBigInteger(),
            P.normalize(),
            Q.normalize().xCoord.toBigInteger(),
            Q.normalize().yCoord.toBigInteger()
        )
    }

    private fun miller(mE: BigInteger, P: ECPoint, x1: BigInteger, y1: BigInteger): BigInteger {

        val bytes = BigInteger.valueOf(calc(37, 54)).toByteArray()
        val binary = StringBuilder()
        mE + BigInteger.valueOf(1)
        for (b in bytes) {
            var `val` = b.toInt()
            for (i in 0..7) {
                binary.append(if (`val` and 128 == 0) 0 else 1)
                `val` = `val` shl 1
            }
        }
        val m = binary.toString().drop(2)
        val n = m.length
        var T = P.normalize()
        var f = BigInteger.valueOf(1)

//        Log.d("SageSign", "miller")
//        Log.d("SageSign", "P=${P.normalize().xCoord}:${P.normalize().yCoord}")
        val p = BigInteger.valueOf(calc(768, 455))
        for (i in 0 until n) {
//            Log.d("SageSign", "T=${T.normalize().xCoord}:${T.normalize().yCoord}")
            val g = g(T.normalize(), T.normalize(), x1, y1).mod(p)
            val f2 = f.pow(2).mod(p)
            f = (f2 * g).mod(p)
            T = T.add(T).normalize()
            val im = m[i].toString().toInt()
            if (im == 1) {
//                Log.d("SageSign", "im=1")
//                Log.d("SageSign", "im=1 T=${T.normalize().xCoord}:${T.normalize().yCoord}")
//                Log.d("SageSign", "im=1 P=${P.normalize().xCoord}:${P.normalize().yCoord}")
                f = (f * (g(T.normalize(), P.normalize(), x1, y1).mod(p))).mod(p)
                T = T.add(P.normalize()).normalize()
            }
        }
        return f
    }

    private fun g(P: ECPoint, Q: ECPoint, x1: BigInteger, y1: BigInteger): BigInteger {
//        Log.d("SageSign", "g")
//        Log.d("SageSign", "P=${P.normalize().xCoord}:${P.normalize().yCoord}\n" +
//                "Q=${Q.normalize().xCoord}:${Q.normalize().yCoord}\nx1=$x1 y1=$y1")
        val p = BigInteger.valueOf(calc(24, 1199))
        val slope: BigInteger = if (P.normalize().xCoord == Q.normalize().xCoord &&
            P.normalize().yCoord.add(Q.normalize().yCoord).isZero
        ) {
            return (x1 - (P.normalize().xCoord.toBigInteger())).mod(p)
        } else if (P.normalize() == Q.normalize()) {
            val num1 =
                P.normalize().xCoord.square().toBigInteger()
                    .multiply(BigInteger.valueOf(calc(1, 2)))
                    .add(BigInteger.valueOf(calc(18, 7)))
                    .mod(p)

            val z = BigInteger.valueOf(2) * P.normalize().yCoord.toBigInteger()
            val den1 = z.mod(p)

            (num1 * den1.modInverse(p)).mod(p)
        } else {

            val num2 =
                (P.normalize().yCoord.toBigInteger() - Q.normalize().yCoord.toBigInteger()).mod(p)
            val den2 =
                (P.normalize().xCoord.toBigInteger() - Q.normalize().xCoord.toBigInteger()).mod(p)

            (num2 * den2.modInverse(p)).mod(p)
        }
        var xpslp = (x1 - P.xCoord.toBigInteger()).mod(p)
        xpslp = (slope * xpslp).mod(p)
        val yp = (y1 - P.yCoord.toBigInteger()).mod(p)

        val slp2 = slope.pow(2).mod(p)

        val num = (yp - xpslp).mod(p)
        val den =
            (((x1 + P.normalize().xCoord.toBigInteger()).mod(p) + Q.normalize().xCoord.toBigInteger()).mod(
                p
            ) - slp2).mod(
                p
            )

        return (num * den.modInverse(p)).mod(p)
    }

    data class GeneratedData(val secretKey: String, val publicKey: String, val signKey: String)
}