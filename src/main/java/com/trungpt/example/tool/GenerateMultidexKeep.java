package com.trungpt.example.tool;

import org.apache.maven.shared.invoker.*;

import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by lent on 11/21/2014.
 */
public class GenerateMultidexKeep
{
    // Modify this to match your local environment
    private static final String PROJECT_ROOT_PATH = "/home/trungpt/Documents/android-annotion-and-mutildexoption";
    private final String MAVEN_HOME_PATH = "/usr/share/maven";

    private String preStripping = "classes";
    private static String[] matchingPackages = {"com/discorp", "android/support/v4", "com/nostra13"};
    private Set<String> results = new TreeSet<String>();

    public static void main(String... args) throws IOException
    {
        final File folder = new File(PROJECT_ROOT_PATH + "/main/target/classes");

        GenerateMultidexKeep multidexKeep = new GenerateMultidexKeep();
        multidexKeep.readOriginalFile();
        multidexKeep.cleanCompileDex();
        multidexKeep.listFilesForFolder(folder);

        DexParsingRunner dexParsingRunner = new DexParsingRunner();
        dexParsingRunner.run(new String[]{PROJECT_ROOT_PATH + "/main/target/classes.dex"}, matchingPackages);

        while (!DexMethodCounts.getExternalRefs().isEmpty())
        {
            System.out.println("Remaining references: " + DexMethodCounts.getExternalRefs().size());
            multidexKeep.results.addAll(DexMethodCounts.getExternalRefs());

            generateMultiDexKeepFile(multidexKeep);

            multidexKeep.cleanCompileDex();
            dexParsingRunner.run(new String[]{"./main/target/classes.dex"}, matchingPackages);
        }
    }

    private void readOriginalFile() throws FileNotFoundException
    {
        Scanner s = new Scanner(new File(PROJECT_ROOT_PATH + "/main/multidex.keep"));
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext())
        {
            list.add(s.next());
        }
        s.close();
        results.addAll(list);
    }

    private static void generateMultiDexKeepFile(GenerateMultidexKeep multidexKeep) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(PROJECT_ROOT_PATH + "/main/multidex.keep"));
        Iterator it = multidexKeep.results.iterator();
        while (it.hasNext())
        {
            out.write(it.next() + "\n");
        }
        out.flush();
        out.close();
    }

    private Invoker invoker;

    private void cleanCompileDex() throws MalformedURLException
    {
        System.out.println("Working dir: " + System.getProperty("user.dir"));
        System.out.println("Android Home dir: " + System.getenv("ANDROID_HOME"));
        InvocationRequest request = new DefaultInvocationRequest();
        request.setBaseDirectory(new File(PROJECT_ROOT_PATH + "/main"));
        request.setPomFile(new File("pom.xml"));
        request.addShellEnvironment("ANDROID_HOME", "/home/trungpt/Documents/android-sdk-linux");
        List<String> profiles = new ArrayList<String>();
        profiles.add("artifactory");
        profiles.add("development");
        request.setProfiles(profiles);

        List<String> goals = new ArrayList<String>();
        goals.add("clean");
        goals.add("compile -q");
        goals.add("android:dex -q");
        request.setGoals(goals);

        if (invoker == null)
        {
            invoker = new DefaultInvoker();
            invoker.setWorkingDirectory(new File(PROJECT_ROOT_PATH + "/main"));
            invoker.setMavenHome(new File(MAVEN_HOME_PATH));
        }

        try
        {
            InvocationResult result = invoker.execute(request);
            if (result.getExitCode() != 0)
            {
                throw new IllegalStateException("Build failed.");
            }
            else
                System.out.println("Execute Maven task - DONE");
        }
        catch (MavenInvocationException e)
        {
            e.printStackTrace();
        }
    }

    private File getBaseDir()
    {
        return new File(System.getProperty("user.dir"));
    }

    public void listFilesForFolder(final File folder)
    {
        if (!folder.exists())
        {
            return;
        }
        for (final File fileEntry : folder.listFiles())
        {
            if (fileEntry.isDirectory())
            {
                listFilesForFolder(fileEntry);
            }
            else
            {
                String path = fileEntry.getPath();
                path = path.substring(path.indexOf(preStripping) + preStripping.length() + 1);
                path = path.replace("\\", "/");

                boolean isMatch = false;
                for (String matching : matchingPackages)
                {
                    if (path.startsWith(matching))
                    {
                        isMatch = true;
                    }
                }
                if (isMatch)
                {
                    results.add(path);
                }
            }
        }
    }

}
