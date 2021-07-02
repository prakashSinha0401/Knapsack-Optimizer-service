package com.domain.kos.utility;

import java.io.Reader;
import java.sql.Clob;
import java.time.Instant;

import javax.sql.rowset.serial.SerialClob;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.domain.kos.service.KosService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KosUtils {
   
	public static final Logger LOGGER = LoggerFactory.getLogger(KosService.class);
	private final static ObjectMapper mapper = new ObjectMapper();
	
	public static String generateRandomString() {
	    int length = 8;
	    boolean useLetters = true;
	    boolean useNumbers = false;
	    return RandomStringUtils.random(length, useLetters, useNumbers);
	}
	
	public static Long getCurrentUtcEpochTime() {
		Instant instant = Instant.now();
		return instant.toEpochMilli();
	}
	
	public static ObjectMapper getMapperObject() {
		return mapper;
	}
	
	public static Clob convertStringToClob(String value) {
		Clob clobValue = null;
		try {
		clobValue =  new SerialClob(value.toCharArray());
		}catch(Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return clobValue;		
	}
	
	public static String convertClobToString(Clob clob) {
		int c = -1;
		StringBuilder sb = new StringBuilder("");
		try {
			Reader reader = clob.getCharacterStream();
			while ((c = reader.read()) != -1) {
				sb.append(((char) c));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}

		return sb.toString();
	}

}
