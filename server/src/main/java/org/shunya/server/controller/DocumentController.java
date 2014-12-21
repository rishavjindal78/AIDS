package org.shunya.server.controller;

import org.apache.commons.io.IOUtils;
import org.shunya.server.model.Document;
import org.shunya.server.model.DocumentStorage;
import org.shunya.server.services.DocumentService;
import org.shunya.server.vo.FileUploadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Controller
@RequestMapping("/documents")
public class DocumentController {
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    @Value("${file.store.path}")
    private String uploadFolder;

    @Autowired
    private DocumentService documentService;

    /*@RequestMapping(value = "documents", method = RequestMethod.GET)
    public String listDocs(@ModelAttribute("model") ModelMap model) {
        logger.info("Total Rows in database are - {}", documentService.countRows());
        model.addAttribute("documents", documentService.findPaginated(0, 20));
        return "documents";
    }*/

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public String searchDocs(@ModelAttribute("model") ModelMap model,
                                @RequestParam(value = "query", required = false, defaultValue = "") String query,
                             @RequestParam(value = "message", required = false, defaultValue = "") String message) {
        model.addAttribute("documents", documentService.searchPaginated(0, 50, query));
        model.addAttribute("query", query);
        model.addAttribute("message", message);
        return "documents";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editAgent(@ModelAttribute("model") ModelMap model, @PathVariable("id") long id) throws Exception {
        Document document = documentService.findById(id);
        model.addAttribute("document", document);
        return "editDocument";
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.POST)
    public String delete(@ModelAttribute("model") ModelMap model, @PathVariable("id") Long id) {
        documentService.delete(id);
        return "redirect:../search";
    }

    @RequestMapping(value = "upload2", method = RequestMethod.POST)
    public String processUpload(@RequestParam MultipartFile file, Model model) throws IOException {
        model.addAttribute("message", "File '" + file.getOriginalFilename() + "' uploaded successfully");
        return "documents";
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public String processUpload2(@ModelAttribute FileUploadDTO file, Model model) throws IOException {
        model.addAttribute("message", "File '" + file.getDescription() + "' uploaded successfully");
        Document document = new Document();
        document.setName(file.getFile().getOriginalFilename());
        document.setDescription(file.getDescription());
        document.setTags(file.getTags());
        document.setLength(file.getFile().getSize());
        document.setUploadDate(new Date());
        if(file.getFile().getSize() > 10*1024*1024){
            //store it in FS
            Path target = Paths.get(uploadFolder, file.getFile().getOriginalFilename());
            Files.copy(file.getFile().getInputStream(), target);
            document.setLocalPath(target.toAbsolutePath().toString());
            document.setStorage(DocumentStorage.FS);
            file.getFile().getInputStream().close();
        }else {
            //store it in DB
            document.setStorage(DocumentStorage.DB);
            document.setContent(file.getFile().getBytes());
        }
        documentService.save(document);
        return "redirect:search";
    }

    @RequestMapping(value = "upload/{documentId}", method = RequestMethod.POST)
    public String processUploadUpdate(@ModelAttribute FileUploadDTO file, Model model, @PathVariable("documentId") long documentId) throws IOException {
        model.addAttribute("message", "File '" + file.getDescription() + "' updated successfully");
        Document document = documentService.findById(documentId);
        document.setName(file.getFile().getOriginalFilename());
        document.setDescription(file.getDescription());
        document.setTags(file.getTags());
        document.setLength(file.getFile().getSize());
        document.setUploadDate(new Date());
        if(file.getFile().getSize() > 10*1024*1024){
            //store it in FS
            Path target = Paths.get(uploadFolder, file.getFile().getOriginalFilename());
            Files.copy(file.getFile().getInputStream(), target);
            document.setLocalPath(target.toAbsolutePath().toString());
            document.setStorage(DocumentStorage.FS);
            file.getFile().getInputStream().close();
        }else {
            //store it in DB
            document.setStorage(DocumentStorage.DB);
            document.setContent(file.getFile().getBytes());
        }
        documentService.save(document);
        return "redirect:../search";
    }

    @RequestMapping(value = "/files/{file_name}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile(@PathVariable("file_name") String fileName) {
        return new FileSystemResource("test file.pdf");
    }

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void getFile(@PathVariable("id") long id, HttpServletResponse response) {
        String fileName = "";
        try {
            // get your file as InputStream
            Document byId = documentService.findById(id);
            fileName = byId.getName();
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName  + "\"");
            response.setHeader("Content-Length", String.valueOf(byId.getLength()));
            response.setContentType("application/octet-stream");
            if(byId.getStorage()==DocumentStorage.DB){
                response.getOutputStream().write(byId.getContent());
            }else{
                FileInputStream fileInputStream = new FileInputStream(byId.getLocalPath());
                IOUtils.copy(fileInputStream, response.getOutputStream());
                fileInputStream.close();
            }
            response.flushBuffer();
            documentService.increaseDownloadCounter(id);
        } catch (IOException ex) {
            logger.info("Error writing file to output stream. Filename was '" + fileName + "'");
            throw new RuntimeException("IOError writing file to output stream");
        }
    }
}
