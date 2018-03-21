package shared;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Encryption
{
	private static  Random              random              = new Random ( );
	private static  char [ ]            base                = null;
	private static  KeyPairGenerator    keyPairGenerator    = null;
	private static  KeyFactory          keyFactory          = null;
	private static  PublicKey           publicKey           = null;
	private static  PrivateKey          privateKey          = null;

	static
	{
		StringBuilder temp = new StringBuilder ( );

		for ( char ch = '0'; ch <= '9'; ch++ )
		{
			temp.append ( ch );
		}

		for ( char ch = 'a'; ch <= 'z'; ch++ )
		{
			temp.append ( ch );
		}

		for ( char ch = 'A'; ch <= 'Z'; ch++ )
		{
			temp.append ( ch );
		}

		base = temp.toString ( ).toCharArray (  );

		Arrays.sort ( base );

		try
		{
			keyPairGenerator    = KeyPairGenerator.getInstance ( "RSA" );
			keyFactory          = KeyFactory.getInstance ( "RSA" );

			keyPairGenerator.initialize ( Shared.RSAResistance );
		}
		catch ( NoSuchAlgorithmException e )
		{
			e.printStackTrace ( );
		}
	}

	public static byte [ ] processXOR ( byte [ ] msg, byte [ ] key )
	{
		int         keyLen  = key.length;
		int         msgLen  = msg.length;
		byte [ ]    tempKey = new byte [ msgLen ];
		byte [ ]    res     = new byte [ msgLen ];

		ArrayList< Byte > newKey = new ArrayList < > ( );

		if ( ( msgLen + keyLen ) % 2 == 0 )
		{
			for ( int i = 0; i < keyLen; i++ )
			{
				if ( i % 2 == 0 )
				{
					newKey.add ( key [ i ] );
				}
			}
		}
		else
		{
			for ( int i = 0; i < keyLen; i++ )
			{
				if ( i % 2 == 1 )
				{
					newKey.add ( key [ i ] );
				}
			}
		}

		int newKeyLen = newKey.size ( );

		if ( msgLen > newKeyLen )
		{
			boolean rev = false;

			for ( int i = 0; i < msgLen; i++ )
			{
				for ( int j = 0; j < newKeyLen; j++ )
				{
					if ( rev )
					{
						tempKey [ i ] = newKey.get ( newKeyLen - j - 1 );
					}
					else
					{
						tempKey [ i ] = newKey.get ( j );
					}
				}

				rev = !rev;
			}
		}
		else
		{
			for ( int i = 0; i < newKeyLen; i++ )
			{
				tempKey [ i ] = newKey.get ( i );
			}
		}

		for ( int i = 0; i < msgLen; i++ )
		{
			res [ i ] = ( byte )( msg [ i ] ^ tempKey [ i ] );
		}

		return res;
	}

	public static byte [ ] generateXORKey ( )
	{
		int len = random.nextInt ( Shared.maxXORKeyLen - Shared.minXORKeyLen ) + Shared.minXORKeyLen;

		byte [ ] buf = new byte [ len ];

		for ( int i = 0; i < buf.length; i++ )
		{
			buf [ i ] = ( byte )base [ random.nextInt ( base.length ) ];
		}

		return buf;
	}

	public static void generateRSAKeyPair ( )
	{
		KeyPair kp = keyPairGenerator.generateKeyPair ( );

		publicKey  = kp.getPublic ( );
		privateKey = kp.getPrivate ( );
	}

	public static PublicKey getPublicRSAKey ( )
	{
		return publicKey;
	}

	public static PrivateKey getPrivateRSAKey ( )
	{
		return privateKey;
	}

	public static byte [ ] encryptRSA ( byte [ ] msgB, PublicKey key )
	{
		try
		{
			Cipher cipher = Cipher.getInstance ( "RSA" );

			cipher.init ( Cipher.ENCRYPT_MODE, key );

			return cipher.doFinal ( msgB );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}

		return null;
	}

	public static byte [ ] decryptRSA ( byte [ ] msgB, PrivateKey key )
	{
		try
		{
			Cipher cipher = Cipher.getInstance ( "RSA" );

			cipher.init ( Cipher.DECRYPT_MODE, key );

			return cipher.doFinal ( msgB );
		}
		catch ( Exception e )
		{
			e.printStackTrace ( );
		}

		return null;
	}

	public static PublicKey restorePublicKey ( byte [ ] msgB )
	{
		EncodedKeySpec  publicKeySpec   = new X509EncodedKeySpec ( msgB );
		PublicKey       publicKey       = null;

		try
		{
			 publicKey = keyFactory.generatePublic ( publicKeySpec );
		}
		catch ( InvalidKeySpecException e )
		{
			e.printStackTrace ( );
		}

		return publicKey;
	}
}