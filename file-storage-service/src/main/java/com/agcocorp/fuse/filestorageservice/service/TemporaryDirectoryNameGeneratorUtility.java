package com.agcocorp.fuse.filestorageservice.service;

import static com.agcocorp.fuse.filestorageservice.service.Md5HashGenerator.generateMd5HashStringFromInputByteArray;
import static java.lang.Thread.currentThread;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;

// TODO: This class is referenced in FileStorageService, TaskDataZipService and ZipService
//  maybe this should be refactored as a service class (Spring bean)
public class TemporaryDirectoryNameGeneratorUtility {

  private TemporaryDirectoryNameGeneratorUtility() {
  }

  public static String generateTemporaryDirectoryName() {
    return encodeThreadNameWithMd5() + now().atZone(systemDefault()).toInstant().toEpochMilli();
  }

  private static String encodeThreadNameWithMd5() {
    return generateMd5HashStringFromInputByteArray(currentThread().getName().getBytes(UTF_8));
  }
}
