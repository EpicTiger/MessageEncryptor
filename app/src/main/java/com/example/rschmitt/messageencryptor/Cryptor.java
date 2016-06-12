package com.example.rschmitt.messageencryptor;

import se.simbio.encryption.Encryption;

public class Cryptor
{
    private final Encryption encryption;
    byte[] iv = {-89, -19, 13, -83, 86, 106, -31, 30, -5, -17, 61, -75, -84, 95, 120, -53};

    public Cryptor()
    {
        encryption = Encryption.getDefault("l33tHax0r0r45213", "S4ltiL1f31337", iv);
    }

    public String encrypt(String message){
        return encryption.encryptOrNull(message);
    }

    public String decrypt(String message){
        return encryption.decryptOrNull(message);
    }
}
