package foo;



import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.rpgframework.eden.foundry.Module;
import de.rpgframework.eden.foundry.sr6.Shadowrun6CompendiumFactory;
import de.rpgframework.genericrpg.data.DataSet;
import de.rpgframework.genericrpg.data.PageReference;
import de.rpgframework.shadowrun6.Shadowrun6Core;
import de.rpgframework.shadowrun6.data.Shadowrun6DataPlugin;

public class InstallCompendium {

    public static void main(String[] args) {
    	Locale.setDefault(Locale.ENGLISH);
		Shadowrun6DataPlugin plugin = new Shadowrun6DataPlugin();
		plugin.init();

         Path target = Paths.get("/home/prelle/.local/share/FoundryVTT/Data/modules/shadowrun6-data");

        try {
        	
            unzipFolder(target);
            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void unzipFolder(Path target) throws IOException {
		List<DataSet> sets = Shadowrun6Core.getDataSets();
		sets.remove(1);
		Function<Collection<PageReference>,Locale[]> callback = (references) -> new Locale[] {Locale.ENGLISH};
		Module modDeep = Shadowrun6CompendiumFactory.createCompendium(null, null, sets, callback, false);
		assertNotNull(modDeep);
		byte[] data = modDeep.fos.toByteArray();
		System.out.println("ZIP length = "+data.length);

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {
                boolean isDirectory = false;
                // example 1.1
                // some zip stored files and folders separately
                // e.g data/
                //     data/folder/
                //     data/folder/file.txt
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, target);

                
                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {

                    // example 1.2
                    // some zip stored file path only, need create parent directories
                    // e.g data/folder/file.txt
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }

                    // copy files, nio
                    long sum= Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                   	System.out.println("Extract "+zipEntry+" with "+sum+" bytes to "+newPath);

                    // copy files, classic
//                    try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
//                        byte[] buffer = new byte[1024];
//                        int sum=0;
//                        int len;
//                        while ((len = zis.read(buffer)) > 0) {
//                            fos.write(buffer, 0, len);
//                            sum+=len;
//                         }
//                    	System.out.println("Extract "+zipEntry+" with "+sum+" bytes to "+newPath);
//                    }
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();

        }

    }

    // protect zip slip attack
    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
        throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }

}
