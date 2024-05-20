/*
 * 
 */
package ariba.sr.supplierextractserver;


import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;

import ariba.sr.utils.DateHelper;

/**
 * 
 * @author i813816
 *
 */
public class LoginAuthController
{
    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
        /*Not doing anything here related to BCFIPS as this is just a test class*/
//        String pass = "Aladdin2018!#200";
//        System.out.println("Aladdin2018!#200 ==>"+generateStrongPasswordHash(pass));
    	
    	
    	
//    	SshClient ssh = new SshClient();
//    	ssh.loadKnownHosts();
//    	ssh.connect("host");
//    	try {
//    	    ssh.authPassword("username", "password");
//    	    SFTPClient sftp = ssh.newSFTPClient();
//    	    try {
//    	        sftp.put(new FileSystemFile("/path/of/local/file"), "/path/of/ftp/file");
//    	    } finally {
//    	        sftp.close();
//    	    }
//    	} finally {
//    	    ssh.disconnect();
//    	}

    	
    	
    	Map<String, Timestamp> status = new HashMap<String, Timestamp>();
    	
    	
    	status.put("test", DateHelper.getCurrentUTCTimestamp());
    	
    	status.put("test", DateHelper.getCurrentUTCTimestamp());
    	
    	
    	status.put("test", DateHelper.getCurrentUTCTimestamp());
    	
        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;
        System.out.println("preparing the host information for sftp.");
        
        try {    	   
     	   
            JSch jsch = new JSch();
            //System.out.println(String.format("Opening sftp connection to %s with username %s", conf.getFtpUrl(), conf.getFtpUsername()));
            session = jsch.getSession("sapdata_test", "k2.semantic-visions.com", 22);
            session.setPassword("wo5dX+S6YQMt");
            
            //session = jsch.getSession("aribanl", "ftp.dnb.com", 22);
            //session.setPassword("Aladdin@2015");
            session.setConfig("StrictHostKeyChecking", "no");
            
            if(true){
	            ProxyHTTP  proxy = new ProxyHTTP("bluecoat-proxy", 8080);
	            proxy.setUserPasswd(null, null);
	            session.setProxy(proxy);
            }
            
            session.setTimeout(60000);

            session.connect();
            System.out.println("Host connected.");
            channel = session.openChannel("sftp");
            channel.connect();

            System.out.println("sftp channel opened and connected.");
            
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd("/production");

        } catch (Exception ex) {
        	System.out.println("Exception found while tranfer the response." + ex.getLocalizedMessage());
        }
		//return channelSftp;

        String pass0 = "Password1";

        System.out.println("Aladdin2018!#200 ==>"+generateStrongPasswordHash(pass0));
        
        String pass1 = "Aladdin2018!#200";

        System.out.println("Aladdin2018!#200 ==>"+generateStrongPasswordHash(pass1));

        String pass2 = "Aladdin17#!?";

        System.out.println("Aladdin17#!? ==>"+generateStrongPasswordHash(pass2));

        String pass3 = "RiskAdmin500%?";

        System.out.println("RiskAdmin500%? ==>"+generateStrongPasswordHash(pass3));

    }

    private static String generateStrongPasswordHash(String password)
        throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt().getBytes();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);

    }

    private static String getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    private static boolean validatePassword(String originalPassword, String storedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec =
                new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);

        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;

        for(int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }

        return diff == 0;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length; i++) {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
