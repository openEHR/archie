package org.s2.rminfo;

import com.nedap.archie.base.RMObject;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

public class S2RmInfoLookupTest {
    @Test
    public void testFindAllClassesUsingClassLoader() throws IOException {
        S2RmInfoLookup infoLookup = S2RmInfoLookup.getInstance();

        Set<Class<?>> classes = infoLookup.findAllClassesInPackage ("org.s2.rm", RMObject.class);
        ArrayList<String> classNames = new ArrayList<>();

        // generate a list of strings and sort
        for (Class<?> cl: classes) {
            classNames.add(cl.getSimpleName());
        }
        Collections.sort(classNames);

        StringBuilder sb = new StringBuilder();
        sb.append ("// \n");
        sb.append ("// include the following in S2RmInfoLookup.addTypes()\n");
        sb.append ("// \n");

        // generate source code
        for (String cName: classNames) {
            sb.append("addClass(" + cName + ".class);\n");
        }

        // Write the code to the file /src/test/resources/openEhrRmCLassList.txt

        Path resourceDirectory = Paths.get("src","test","resources");
        String absPath = resourceDirectory.toFile().getAbsolutePath() + "/RmCLassList.txt";
        FileWriter fw = new FileWriter(absPath);
        fw.write(sb.toString());
        fw.close();
    }

}
