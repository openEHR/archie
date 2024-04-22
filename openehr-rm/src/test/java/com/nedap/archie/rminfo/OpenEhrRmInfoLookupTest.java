package com.nedap.archie.rminfo;

import com.nedap.archie.base.RMObject;
import com.nedap.archie.openehr.rminfo.OpenEhrRmInfoLookup;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class OpenEhrRmInfoLookupTest {
    @Test
    public void testFindAllClassesInRm() throws IOException {
        OpenEhrRmInfoLookup infoLookup = OpenEhrRmInfoLookup.getInstance();

        Set<Class<?>> classes = infoLookup.findAllClassesInPackage ("org.openehr.rm", RMObject.class);
        ArrayList<String> classNames = new ArrayList<>();

        // generate a list of strings and sort
        for (Class<?> cl: classes) {
            classNames.add(cl.getSimpleName());
        }
        Collections.sort(classNames);

        StringBuilder sb = new StringBuilder();
        sb.append ("// \n");
        sb.append ("// include the following in OpenEhrRmInfoLookup.addTypes()\n");
        sb.append ("// \n");

        // generate source code
        for (String cName: classNames) {
            sb.append("addClass(" + cName + ".class);\n");
        }

        // Write the code to the file /src/test/resources/RmCLassList.txt
        Path resourceDirectory = Paths.get("src","test","resources");
        String absPath = resourceDirectory.toFile().getAbsolutePath() + "/RmCLassList.txt";
        FileWriter fw = new FileWriter(absPath);
        fw.write(sb.toString());
        fw.close();
    }

}
