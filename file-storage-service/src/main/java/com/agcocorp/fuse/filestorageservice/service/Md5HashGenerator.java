package com.agcocorp.fuse.filestorageservice.service;

import static org.slf4j.LoggerFactory.getLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;

// TODO: This class is referenced in FileStorageService, TaskDataZipService and ZipService
//  maybe this should be refactored as a service class (Spring bean)
public final class Md5HashGenerator {

  private static final Logger logger = getLogger(Md5HashGenerator.class);
  private static final String MD5 = "MD5";

  private Md5HashGenerator() {
  }

  static String generateMd5HashStringFromInputByteArray(byte[] inputStringByteArray) {
    try {
      return DatatypeConverter
          .printHexBinary(MessageDigest.getInstance(MD5).digest(inputStringByteArray))
          .toLowerCase();
    } catch (NoSuchAlgorithmException e) {
      logger.error(e.getMessage());
    }
    return null;
  }
}
