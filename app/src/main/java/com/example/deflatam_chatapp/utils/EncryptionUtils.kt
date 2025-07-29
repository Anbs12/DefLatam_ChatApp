package com.example.deflatam_chatapp.utils


import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utilidad para implementar cifrado/descifrado básico de extremo a extremo (end-to-end).
 * Utiliza AndroidKeyStore para almacenar la clave AES de forma segura.
 * **Nota**: Esta es una implementación básica. Para un cifrado E2E robusto, se requieren protocolos criptográficos más complejos
 * que involucren intercambio de claves (ej. Diffie-Hellman), gestión de identidad y no solo cifrado simétrico.
 */
@Singleton
class EncryptionUtils @Inject constructor() {

    private val KEY_STORE_PROVIDER = "AndroidKeyStore"
    private val keyStore: KeyStore = KeyStore.getInstance(KEY_STORE_PROVIDER).apply { load(null) }

    init {
        createKeyIfNotFound()
    }

    /**
     * Crea una clave secreta si no existe en AndroidKeyStore.
     */
    private fun createKeyIfNotFound() {
        if (!keyStore.containsAlias(Constants.ENCRYPTION_KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEY_STORE_PROVIDER
            )
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                Constants.ENCRYPTION_KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7) // Usar PKCS7 para compatibilidad
                .setRandomizedEncryptionRequired(true) // Siempre usar un IV aleatorio
                .build()
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    /**
     * Obtiene la clave secreta del AndroidKeyStore.
     * @return La clave secreta si existe, null de lo contrario.
     */
    private fun getSecretKey(): SecretKey? {
        return (keyStore.getEntry(Constants.ENCRYPTION_KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.secretKey
    }

    /**
     * Cifra una cadena de texto.
     * @param plainText El texto a cifrar.
     * @return El texto cifrado en formato Base64, con el IV prepuesto, o null si falla.
     */
    fun encrypt(plainText: String): String? {
        val secretKey = getSecretKey() ?: return null
        return try {
            val cipher = Cipher.getInstance(Constants.ENCRYPTION_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv // Obtener el IV generado por el cifrado.
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

            // Prepend IV to the encrypted data for decryption
            val combined = iv + encryptedBytes
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Descifra una cadena de texto cifrada.
     * @param encryptedText El texto cifrado en formato Base64 (con IV prepuesto).
     * @return El texto original descifrado, o null si falla.
     */
    fun decrypt(encryptedText: String): String? {
        val secretKey = getSecretKey() ?: return null
        return try {
            val decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT)

            // Extract IV from the beginning of the decoded bytes (16 bytes for AES)
            val iv = decodedBytes.copyOfRange(0, 16)
            val encryptedBytes = decodedBytes.copyOfRange(16, decodedBytes.size)

            val cipher = Cipher.getInstance(Constants.ENCRYPTION_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))
            val decryptedBytes = cipher.doFinal(encryptedBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
