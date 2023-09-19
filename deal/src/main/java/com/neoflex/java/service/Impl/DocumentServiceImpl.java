package com.neoflex.java.service.Impl;

import com.neoflex.java.model.Application;
import com.neoflex.java.service.DocumentService;
import com.neoflex.java.util.CustomLogger;
import lombok.extern.log4j.Log4j2;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
@Log4j2
public class DocumentServiceImpl implements DocumentService {
    private final String documentFolder;

    public DocumentServiceImpl(@Value("${documentFolder}") String documentFolder) {
        this.documentFolder = documentFolder;
    }

    @Override
    public void createDocument(Application application) {
        CustomLogger.logInfoClassAndMethod();
        try {
            Files.createDirectories(Paths.get(documentFolder));
            saveDocxFile("Кредитный договор", application.getCredit().toString(), application.getId());
            saveDocxFile("Анкета", application.getClient().toString(), application.getId());
            saveDocxFile("График платежей", Arrays.toString(application.getCredit().getPaymentSchedule().toArray()), application.getId());
            log.info("Файлы docx созданы");
        } catch (Docx4JException|IOException e) {
            log.info("Ошибка создания файла docx");
        }
    }

    private void saveDocxFile(String title, String paragraphText, Long id) throws Docx4JException {
        CustomLogger.logInfoClassAndMethod();
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        mainDocumentPart.addStyledParagraphOfText("Title", title);
        mainDocumentPart.addParagraphOfText(paragraphText);
        wordPackage.save(new File(documentFolder + id + " " + title + ".docx"));
    }
}
