package org.shunya.server.services;

import org.shunya.server.model.Document;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DocumentService extends GenericService<Document, Long>{
    void increaseDownloadCounter(Long id);

    List<Document> searchPaginated(int page, int size, String query);

    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    void delete(Long id);
}
