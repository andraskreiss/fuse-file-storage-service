package com.agcocorp.fuse.filestorageservice.service;

import static com.agcocorp.fuse.filestorageservice.service.TemporaryDirectoryNameGeneratorUtility.generateTemporaryDirectoryName;
import static com.agcocorp.fuse.filestorageservice.service.directoryutils.TemporaryDirectoryDetector.getTemporaryDirectory;
import static java.io.File.separator;
import static java.lang.String.format;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Arrays.fill;
import static org.apache.commons.lang3.ArrayUtils.subarray;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static org.springframework.util.FileSystemUtils.deleteRecursively;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@Scope(SCOPE_PROTOTYPE)
public class FileStorageService implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private static final int BUFFER_SIZE = 65536;

    private String uploadDirectorySuffix;

    private static String workingDirectory;
    private static String fileUploadDirectory;
    private Path uploadFilePath;

    private final Environment environment;

    public FileStorageService(Environment environment) {
        this.environment = environment;
        uploadDirectorySuffix = this.environment.getProperty("file_storage_service.upload_directory");

        workingDirectory = getTemporaryDirectory();
        fileUploadDirectory = workingDirectory + separator + uploadDirectorySuffix;
    }

    public Path storeBytesAsFile(byte[] data, String fileName) {
        uploadFilePath = get(fileUploadDirectory, generateTemporaryDirectoryName());
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
            createDirectories(uploadFilePath);
            Path targetFileFullPath = get(uploadFilePath.toAbsolutePath().toString(), fileName);
            copy(inputStream, targetFileFullPath, REPLACE_EXISTING);
            return targetFileFullPath;
        } catch (IOException ioException) {
            throw new FileStorageException(format("Error occurred during storing file %s. %s", fileName, ioException), ioException);
        }
    }


    private static Path createDir(Path path) {
        try {
            return createDirectories(path.normalize());
        } catch (Exception e) {
            throw new FileStorageException(format("Could not create %s directory!", path.toString()), e);
        }
    }

    public void removeTemporaryData(Path mainExportDirectory, Path outputZipFilePath) {
        try {
            // TODO GC: delete input?
            deleteRecursively(mainExportDirectory);
            deleteRecursively(outputZipFilePath.getParent());
        } catch (Exception e) {
            throw new FileStorageException("Could not remove temp folders! Please try again!", e);
        }
    }

    public byte[] convertFileToByteArray(File file) {
        try {
            logger.info("Output stream generation has been initiated");
            try (FileInputStream fileInputStream = new FileInputStream(file);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(BUFFER_SIZE)) {
                byte[] bytes = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                while (bytesRead != -1) {
                    bytesRead = fileInputStream.read(bytes);
                    if (bytesRead > 0) {
                        byteArrayOutputStream.write(subarray(bytes, 0, bytesRead));
                        fill(bytes, (byte) 0);
                        bytesRead = 0;
                    }
                }
                logger.info("Output stream generation has been completed successfully\n");
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new FileStorageException(format("Cannot convert file to byte stream. %s", e.getMessage()), e);
        }
    }

    @Override
    public void afterPropertiesSet() {
        logger.info("Upload directory: {}", fileUploadDirectory);

        Path fileUploadDirectoryPath = get(fileUploadDirectory);

        try {
            deleteRecursively(fileUploadDirectoryPath);
            logger.info("File upload directory content has been deleted");
        } catch (IOException ioException) {
            logger.error("Cannot delete contents of file upload directory. {}", fileUploadDirectory, ioException);
        }

        try {
            createDirectories(get(fileUploadDirectory).normalize());
        } catch (IOException ioException) {
            throw new FileStorageException("Cannot create target directory", ioException);
        }
    }

    @Override
    public void destroy() {
    }
}
