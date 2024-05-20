/*
    Copyright (c) 1996-2008 Ariba, Inc.
    All rights reserved. Patents pending.

    $Id$

    Responsible: vpokrovskii
*/

package ariba.install.util;

import ariba.tool.util.CommonKeys;
import ariba.tool.util.ToolsUtil;
import ariba.util.core.Fmt;
import ariba.util.core.IOUtil;
import ariba.util.core.ListUtil;
import ariba.util.core.StringUtil;
import ariba.util.core.SystemUtil;
import ariba.util.core.FastStringBuffer;
import ariba.util.core.FileUtil;
import ariba.util.parameters.EncryptTool;
import com.installshield.product.SoftwareObjectKey;
import com.installshield.product.SoftwareVersion;
import com.installshield.wizard.service.ServiceException;
import com.zerog.ia.api.pub.InstallShieldUniversalRegistry;
import com.zerog.ia.api.pub.InstallShieldUniversalSoftwareObject;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

/**
   Simple utilities used by the installation API.

   @aribaapi ariba
*/
public class Util
{
   /**
      Retrieves value of a key from a string containing the "key=value" context.

      @aribaapi private
    */
    public static String getValueOf(String k, String s)
    {
            // Retrieve value of a key from a string containing
            // the key="value" context.

        String a;
        a = s.substring(s.indexOf(k + "="));
        a = a.substring(a.indexOf("\"")+1);
        a = a.substring(0,a.indexOf("\""));
        return a;
    }

   /**
      Retrieves value of an element e from a string containing the
      <code>String</code>e<code>String</code> context.

      @aribaapi ariba
    */
    public static String getElementOf(String s, String s1, String s2)
    {
            // Retrieve value of an element e from a string containing
            // the <s1>e<s2> context.

        String a = null;
        try {
            a = s.substring(0,s.indexOf(s2));
            a = a.substring(s.indexOf(s1) + s1.length());
        }
        catch(Exception e ) {
            System.err.println(e);
        }
        return a;
    }

    /**
      Inserts value of a key to a string containing the key=value context.

      @aribaapi ariba
    */
    public static String setValueOf(String key, String temp, String value)
    {
            // Insert value of a key to a string containing
            // the key="value" context.

        String a;
        String b;
        String c;
        String line = "";

        int ind = temp.indexOf(key + "=");

        a = temp.substring(ind);
        b = a.substring(a.indexOf("\"")+1);
        c = b.substring(b.indexOf("\"")+1);

        line = temp.substring(0,ind) + key + "=\"" + value + "\"" + c;
        return line;
    }

    /**
      Returns list of tokens retrieved from a given string.

      @aribaapi ariba
    */
    public static String[] getTokens(String s, String separator)
    {
            // Retrieve tokens from a string of the format
            // token1<separator>token2<separator>...

        int i = 0;

        StringTokenizer st = new StringTokenizer(s,separator);
        String[] a = new String[st.countTokens()];
        while (st.hasMoreTokens()) {
            a[i] = st.nextToken();
            i++;
        }
        return a;
    }

        // Don't execute System.setProperies from IS applications,
        // Security Manager won't like it!

    public static void setSysProperties(Properties pr)
    {
        System.setProperties(pr);
    }

    /**
      Replaces backslashes in a string with double backslashes.

      @aribaapi ariba
    */
    public static String setDoubleBackslash(String s)
    {
        String[] tokens = getTokens(s,"\\");
        String h = "";
        for (int i = 0; i < tokens.length; i++) {
            h = h + tokens[i] + "\\\\";
        }
        return h.substring(0,h.length()-2);
    }

    /**
      Replaces double backslashes in a string with single backslashes.

      @aribaapi ariba
    */
    public static String unsetDoubleBackslash(String s)
    {
        String[] tokens = getTokens(s,"\\\\");
        String h = "";
        for (int i = 0; i < tokens.length; i++) {
            h = h + tokens[i] + "\\";
        }
        return h.substring(0,h.length()-1);
    }

    /**
      Returns current timestamp in the format YYYY-MM-DD_hh.mm.ss.

      @aribaapi ariba
    */
    public static String getTimeStamp()
    {
        int year = 0 ;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int second = 0;

        Calendar now = Calendar.getInstance();

        year   = now.get(Calendar.YEAR);
        month  = now.get(Calendar.MONTH) + 1;
        day    = now.get(Calendar.DATE);
        hour   = now.get(Calendar.HOUR_OF_DAY);
        minute = now.get(Calendar.MINUTE);
        second = now.get(Calendar.SECOND);

        StringBuffer sbf = new StringBuffer();
        sbf.append (year);

        if(month < 10)
            sbf.append("-0");
        else
            sbf.append("-");

        sbf.append(month);

        if(day < 10)
            sbf.append("-0");
        else
            sbf.append("-");

        sbf.append (day);

        if(hour < 10)
            sbf.append("_0");
        else
            sbf.append("_");

        sbf.append (hour);

        if(minute < 10)
            sbf.append(".0");
        else
            sbf.append(".");

        sbf.append(minute);

        if(second < 10)
            sbf.append(".0");
        else
            sbf.append(".");

        sbf.append(second);

        return sbf.toString();
    }

    /**
      Removes string context delimiters.

      @aribaapi ariba
    */
    public static void unQuote(String[] s, String q)
    {
        for (int i = 0; i < s.length; i++) {
            String a = s[i];
            if (a.startsWith(q))
                a = a.substring(1,a.length()-1);
            if (a.endsWith(q))
                a = a.substring(0,a.length()-2);
            s[i] = a;
        }
    }

    /**
       Sets a message by replacing the %s placeholders with actual values.

       @aribaapi ariba
    */
    public static String setMessage(String[] t, String s)
    {
        int i = 0;
        for (int j = 0; j < t.length; j++) {
            if ((i = s.indexOf("%s")) == -1) {
                return(s);
            }
            s = s.substring(0,i) + t[j] + s.substring(i+2);
        }
        return s;
    }
    /**
       Returns a string with first letter capitalized.

       @aribaapi ariba
    */
    public static String firstCap(String s)
    {
        if (s.length() == 0) {
            return s;
        }
        else if (s.length() == 1) {
            return s.toUpperCase();
        }
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    /**
       Reads the contents of a text file into a string.

       @aribaapi ariba
    */
    public static String readFileToString(File f, String s)
    {
        String out = "";
        try {
            BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f)));
            String line = null;
            while ((line = br.readLine()) != null) {
                out = out + line + s;
            }
            br.close();
        }
        catch(Exception e) {
            out = out + e + s;
            return out;
        }
        return out;
    }

    /**
     * Constructs and returns the j2eesetup command. It also writes the command into a file called
     * last.cmd or last.sh depending on the paltform
     *
     * @param j2eeSetupPath The location where j2eesetup is present (no need to add .exe for windows).
     * @param appServerRoot The the root location of the appserver
     * @param appServerType The type of appserver
     * @param appType The type of the application
     * @param configFile The location of the config file (e.g. ./etc/install/install.sp)
     * @param configType The type of configuration
     * @param logFile The location of the log file
     * @param script The file that contains the command. Appends ".cmd" if windows or ".sh" if unix.
     * @param otherArgs A String array contating any other arguments that need to be passed to the command
     * @param logger. Used for logging.
     * @return A String[] containg the command where each argument is an element in the String[]. String[1] is returned
     * if any of the arguments are incorrect.
     */
    public static int runJ2EESetupCommand (String j2eeSetupPath, String appServerRoot,
      String appServerType, String appType, String configFile, String configType,
      String logFile, Logger logger, String script, String[] otherArgs)
    {
        int exitValue = 777;
        List args = ListUtil.list();
        if ((j2eeSetupPath==null) || (configFile==null) || (configType==null) ||
                (appServerType==null) || (appType==null) ||
                (appServerRoot==null) || (script==null)) {

            
        	logger.log(Level.SEVERE, Fmt.S("one of the below"+
              "arguments is invalid\n" + "j2eeSetupPath : %s\n configFile : %s\n"+
              "configType : %s\n " + "appServerRoot : %s\n appserverType : %s\n"+
              "appType : %s\n ",
              j2eeSetupPath,configFile,configType,appServerRoot
              ,appServerType,appType));
             
            return 1;
        }

        if (script==null) {
             logger.log(Level.SEVERE, "Script file that conatains"+
              "the j2eesetup command is not defined");
             
            return exitValue;
        }

        //append extensions
        if (SystemUtil.isWin32()) {
            j2eeSetupPath = StringUtil.strcat(j2eeSetupPath,".exe");
            script = StringUtil.strcat(script,".cmd");
        }
        else {
            script = StringUtil.strcat(script,".sh");
        }
        //add the path
        args.add(j2eeSetupPath);

        //add webserver type
        args.add(appServerType);

        //add log file if any
        if (logFile!=null) {
            args.add("-logFile");
            args.add(logFile);
        }

        //add config file
        args.add("-configFile");
        args.add(configFile);

        String updateConfig = null;
        String customConfig = null;
        String standardConfig = null;

        if (CommonKeys.WebLogic.equals(appServerType)) {
            updateConfig = appendHomeDir(Fmt.S(CommonKeys.updateConfigPathWL, appType));
            customConfig = Fmt.S(CommonKeys.deployConfigPathWL,appType);
            standardConfig = Fmt.S(CommonKeys.defaultConfigPathWL,appType);
        }
        else if (CommonKeys.WebSphere.equals(appServerType)) {
            updateConfig = appendHomeDir(Fmt.S(CommonKeys.updateConfigPathWS, appType));
            customConfig = Fmt.S(CommonKeys.deployConfigPathWS,appType);
            standardConfig = Fmt.S(CommonKeys.defaultConfigPathWS,appType);
        }
        else if ( CommonKeys.Tomcat.equals(appServerType))
        {
        	
        	// TO DO - do the tomcat related stuff here
        	args.clear() ;
        	args.add(j2eeSetupPath) ;
        	args.add("buyer") ;
        	args.add("Buyer") ;
        	args.add(System.getProperty(Fmt.S(CommonKeys.ProductHome, CommonKeys.Tomcat)));
            String[] configureTomcat = new String[args.size()];
            args.toArray(configureTomcat);

             logger.log(Level.INFO,"Command that will be executed is " +
              args.toString());
             
            
            FastStringBuffer fsb = new FastStringBuffer();
        	exitValue = ToolsUtil.invokeScript(configureTomcat, null, fsb, 1800, true);
        	return exitValue ;

        }
        else {
            
             logger.log(Level.SEVERE,Fmt.S("Unknown App Server"+
             "%s.",appServerType));
             
            return exitValue;
        }

        // TODO as now we don't support custom config lets comment it. Get it
        // reviewed
        if (configType.equals(CommonKeys.StandardConfig)) {
            args.add("-xmlFile");
            args.add(standardConfig);

            args.add("-xmlFile");
            args.add(customConfig);
        }
        else if (configType.equals(CommonKeys.CustomConfig)) {
            args.add("-xmlFile");
            args.add(customConfig);
        }
        else {
            //updateconfig
            args.add("-xmlFile");
            args.add(updateConfig);
            //the classpath, librarypath needs to be generated.
            args.add("-genAppPaths");
        }

        //add j2eeServerRoot
        args.add("-j2eeServerRoot");
        args.add(appServerRoot);


        //add rest of the arguments if any
        if (otherArgs!=null) {
            for (int i=0;i<otherArgs.length;++i) {
                args.add(otherArgs[i]);
            }
        }

        String password = null;

        if (CommonKeys.WebLogic.equals(appServerType)) {
            args.add("-adminPasswordViaStdin");
            password = System.getProperty(CommonKeys.J2EEWebLogicPassword);
            if (EncryptTool.isJCEEncrypted(password)) {
                password = EncryptTool.decrypt(password);
            }
        }

        if (!lastCommand(script, args)) {
            
             logger.log(Level.WARNING, Fmt.S("Couldnot create file "+
              "%s.",script));
             
        }

        String[] j2eeSetup = new String[args.size()];
        args.toArray(j2eeSetup);
        
         logger.log(Level.INFO,"Command that will be executed is " +
          args.toString());
         
        logger.log(Level.INFO,"Arguments are " + args.toArray());


        ByteArrayInputStream bi = new ByteArrayInputStream(
            StringUtil.strcat(password,"\n").getBytes());
        FastStringBuffer fsb = new FastStringBuffer();

        // Set timeout arbitrarily to 15 min
        exitValue = ToolsUtil.invokeScript(j2eeSetup, bi, fsb, 1800, true);

        if (exitValue !=0) {
            logger.log(Level.SEVERE, "J2EESetup Failed ");
                //log the error
            String temp = fsb.toString();
            if (!StringUtil.nullOrEmptyOrBlankString(temp)) {
                logger.log(Level.SEVERE,temp); 
            }
            logger.log(Level.SEVERE, "j2eesetup stdout "); 
                //log the stdout also just in case
            temp = fsb.toString();
            if (!StringUtil.nullOrEmptyOrBlankString(temp)) {
                logger.log(Level.SEVERE,temp); 
            }

        }

        return exitValue;

    }

    /**
     * Writes the j2eesetup command to the file.
     *
     * @param script The script file that contains the command
     * @param cmd The command itself as a List
     * @param wb The wizard bean for logging purposes.
     * @return If true the write to the file is successful.
     */
    // TODO removed wizardbean
    private static boolean lastCommand (String script, List cmd)
    {
        File scriptFile = new File(script);
        script = scriptFile.getAbsolutePath();
        if (scriptFile.exists()) {
            scriptFile.delete();
        }
        try
        {
            // create directory if it doesn't exist.
            String dir = scriptFile.getParent();
            File dirName = new File(dir);
            FileUtil.directory(new File(dirName.getAbsolutePath()));

            scriptFile = new File (scriptFile.getAbsolutePath());
            RandomAccessFile raf = new RandomAccessFile(scriptFile, "rw");
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);

            try {
                if (!SystemUtil.isWin32()) {
                    bw.write("#!/bin/sh\n");
                }
                else {
                    bw.write("cmd /c ");
                }

                for (int i=0 ; i<cmd.size(); ++i) {
                    //If argument contains a space then enclose it in double quotes
                    String temp = (String)cmd.get(i);
                    if (temp.indexOf(" ") != -1) {
                        bw.write("\""+temp+"\"");
                    }
                    else {
                        bw.write(temp);
                    }
                    bw.write(" ");
                }
                bw.write("\n");
                bw.flush();
                bw.close();
                /*
                 * TODO Log wb.logEvent(wb,Log.DBG,Fmt.S("Command being written
                 * into file is %s", sw.getBuffer().toString()));
                 */
                raf.write(sw.getBuffer().toString().getBytes());
                raf.close();
                raf = null;
            }
            catch(Exception e) {
                /* TODO Log wb.logEvent(wb, Log.ERROR, e); */
                System.err.println(e);
                return false;
            }
        }
        catch(Exception e ) {
            /* TODO wb.logEvent(wb, Log.ERROR, e); */
            System.err.println(e);
            return false;
        }

        String command = "chmod +x " + script;

        int exitCode = 1;

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            exitCode = process.exitValue();

            if ( exitCode != 0 ) {
                /*
                 * TODO Log wb.logEvent(wb, Log.WARNING, "Problem while changing
                 * permissions on file " + script);
                 */
                System.err.println("Problem while changing permissions on file" + script);
                return false;
            }
        }
        catch(Exception e) {
            /* TODO LOG wb.logEvent(wb, Log.ERROR, e); */
            System.err.println(e);
            return false;
        }

        return true;
    }

    /**
     * Given a file it backs it up. Before backing up it strips the extension if any
     * (assumes anything after the last "." is the extension),
     * appends a time stamp and then appends the extension.
     * @param fileName The file to be backed up
     * @param wb WizardBean for logging
     * @return true on success
     */
    // TODO removed log
    public static boolean backupFile (String fileName)
    {

        if (StringUtil.nullOrEmptyOrBlankString(fileName)) {
            /* TODO wb.logEvent(wb,Log.DBG,"File to back up is not defined."); */
            return false;
        }
            //File should exist for back up. otherwie return true.
        File sourceFile = new File (fileName);
        if (!sourceFile.exists()) {
            /*
             * TODO LOg wb.logEvent(wb,Log.DBG,Fmt.S("File %s does not exist. No
             * need to back up", fileName));
             */return true;
        }
        if (sourceFile.isDirectory()) {
            /*
             * TODO wb.logEvent(wb,Log.DBG, Fmt.S("File %s is a directory.
             * Cannot back up", fileName));
             */
            return false;
        }


        String backUpFileName = null;
        if (sourceFile.getParent()!=null) {
            backUpFileName = Fmt.S("%s/%s",sourceFile.getParent(),
                ToolsUtil.createArchiveFilename(sourceFile));
        }
        else {
            backUpFileName = ToolsUtil.createArchiveFilename(sourceFile);
        }

        if (!IOUtil.copyFile(new File(fileName), new File(backUpFileName))) {
            /*
             * TODO wb.logEvent(wb,Log.DBG, Fmt.S("Could not back up %s to
             * %s.",fileName,backUpFileName));
             */
            return false;
        }
        return true;
    }

    /**
        Retrieves full paths for software objects with a given UID
        from the VPD registry.
        @param wb Caller wizard bean.
        @param rs Handle to the RegistryService.
        @param uid Software object UID.
        @return String array of object paths (empty array if no
        objects found) or null if the registry cannot be accessed.
        @throws com.installshield.wizard.service.ServiceException
        if an error occured during the operation.
    */
    public static String[] getSoftwareObjectPaths(InstallShieldUniversalRegistry rs,
                                                   String uid) throws ServiceException
    {
        String[] a = null;
        /* TODO String vpd = rs.getVPDFileName(); */
        String vpd = rs.getVpdLocation();
        File vpdFile = new File(vpd);

        if (!(vpdFile.exists() && vpdFile.canRead())) {
            /*
             * TODO wb.logEvent(wb, Log.WARNING, StringUtil.strcat( "Could not
             * access the registry ", vpd));
             */
            return a;
        }
        else {
            /*
             * TODO wb.logEvent(wb, Log.DBG, StringUtil.strcat( "Querying the
             * registry ", vpd));
             */
        }

        InstallShieldUniversalSoftwareObject[] sob = rs.getSoftwareObjects(uid);

        /* TODO See SoftwareObject[] sob = rs.getSoftwareObjects(uid); */
        a = new String[sob.length];
        for (int i = 0; i < sob.length; i++) {
            a[i] = sob[i].getInstallLocation();
        }
        return a;
    }

    /**
        Retrieves ASM group members by querying the VPD registry.
        @param wb Caller wizard bean.
        @param rs Handle to the RegistryService.
        @param group ASM group name.
        @return Hashtable of product -> path (empty table if no
        objects found) or null if the registry cannot be accessed.
        @throws com.installshield.wizard.service.ServiceException
        if an error occured during the operation.
    */
    public static Hashtable getAsmGroupMembers (InstallShieldUniversalRegistry rs,
                                                String group) throws ServiceException
    {
        Hashtable a = new Hashtable();
        /* TODO String vpd = rs.getVPDFileName(); */
        String vpd = rs.getVpdLocation();
        File vpdFile = new File(vpd);

        if (!(vpdFile.exists() && vpdFile.canRead())) {
            /*
             * wb.logEvent(wb, Log.WARNING, StringUtil.strcat( "Could not access
             * the registry ", vpd));
             */
            return null;
        }
        else {
            /*
             * wb.logEvent(wb, Log.DBG, StringUtil.strcat( "Querying the
             * registry ", vpd));
             */
        }

        Enumeration products = CommonKeys.AsmProductServerUid.keys();
        while(products.hasMoreElements()) {
            String product = (String)products.nextElement();
            String uid = (String)CommonKeys.AsmProductServerUid.get(product);
            InstallShieldUniversalSoftwareObject[] instance = rs.getSoftwareObjects(uid);

            /* TODO See SoftwareObject[] instance = rs.getSoftwareObjects(uid); */
            for (int j = 0; j < instance.length; j++) {
                String pdir = instance[j].getInstallLocation();
                /*
                 * TODO SoftwareObjectKey psok = instance[j].getKey();
                 * SoftwareVersion psv = psok.getVersion(); String s =
                 * psv.getFormatted();
                 */
                String s = instance[j].getVersion();

                if (s.equals(group)) {
                    a.put(product,pdir);
                }
            }
        }
        return a;
    }

    /**
     * Returns true if argument ASM group has default ASM group name.
    */
    public static boolean isDefaultASMGroup(String g)
    {
        if (StringUtil.nullOrEmptyOrBlankString(g)) {
            return false;
        }
        return g.equalsIgnoreCase(CommonKeys.AsmGroupDefaultValue);
    }

    /**
        Returns true if subject ASM group has a valid group name.
    */
    public static boolean isValidASMGroupName(String g)
    {
        if (isDefaultASMGroup(g)) {
            return true;
        }
        if (g.length() > CommonKeys.MaxASMGroupNameLength) {
            return false;
        }
        if (!(startsWithLetter(g) && hasLettersOrDigitsOrUnderscore(g))) {
           return false;
        }
        return true;
    }

    /**
        Returns true if subject deployment group has a valid group name.
    */
    public static boolean isValidDeploymentGroupName(String g)
    {
        if (isDefaultASMGroup(g)) {
            return false;
        }
        if (g.length() > CommonKeys.MaxDeploymentGroupNameLength) {
            return false;
        }
        if (!(startsWithLetter(g) && hasLettersOrDigitsOrUnderscore(g))) {
           return false;
        }
        return true;
    }

    /**
        Returns true if subject string starts with a letter.
    */
    public static boolean startsWithLetter(String s)
    {
        if (StringUtil.nullOrEmptyOrBlankString(s)) {
            return false;
        }
        char c = s.charAt(0);
        if ((new Character(c)).isLetter(c)) {
            return true;
        }
        return false;
    }

    /**
        Returns true if subject string contains only letters, digits and
        underscore characters.
    */
    public static boolean hasLettersOrDigitsOrUnderscore(String s)
    {
        if (StringUtil.nullOrEmptyOrBlankString(s)) {
            return false;
        }
        char[] c = s.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (!(c[i] == '_' || (new Character(c[i])).isLetterOrDigit(c[i]))) {
                return false;
            }
        }
        return true;
    }

    public static String appendHomeDir (String xmlFile)
    {
        String home = System.getProperty(CommonKeys.AribaHome);
        if (home == null) {
            home = System.getProperty(
                Fmt.S(CommonKeys.ProductServerHome, Global.PRODUCT));
         }
        if (home != null) {
            xmlFile = home + File.separator + xmlFile;
        }
        return xmlFile;
    }

}
