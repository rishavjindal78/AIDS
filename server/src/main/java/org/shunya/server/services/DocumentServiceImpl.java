package org.shunya.server.services;

import org.hibernate.Hibernate;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.shunya.server.dao.DocumentDao;
import org.shunya.server.dao.GenericDao;
import org.shunya.server.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("documentService")
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
public class DocumentServiceImpl extends GenericServiceImpl<Document, Long> implements DocumentService {
    @Autowired
    private DocumentDao documentDao;

    @Override
    public Document findById(Long id) {
        final Document byId = documentDao.findById(id);
        if (byId.getAttachmentContent() != null)
            Hibernate.initialize(byId.getAttachmentContent());
        return byId;
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void increaseDownloadCounter(Long id) {
        final Document byId = documentDao.findById(id);
        byId.setDownloads(byId.getDownloads() + 1);
    }

    @Override
    public List<Document> findPaginated(int page, int size) {
        return documentDao.findAllPaginated(page, size, Order.desc("id"), Restrictions.like("name", "%"));
    }

    @Override
    public List<Document> searchPaginated(int page, int size, String query, long teamId) {
        return documentDao.findAllPaginated(page, size, Order.desc("uploadDate"), Restrictions.and(Restrictions.eq("team.id", teamId), Restrictions.or(Restrictions.ilike("name", "%" + query + "%", MatchMode.ANYWHERE), Restrictions.ilike("description", "%" + query + "%", MatchMode.ANYWHERE), Restrictions.ilike("tags", "%" + query + "%", MatchMode.ANYWHERE))));
    }

    @Override
    @Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED)
    public void delete(Long id) {
        documentDao.delete(documentDao.findById(id));
    }

    @Override
    public GenericDao getDao() {
        return documentDao;
    }
}
