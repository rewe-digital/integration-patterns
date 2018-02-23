package com.rewedigital.examples.msintegration.productdetailpage.infrastructure.eventing.unprocessable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class UnprocessableEventService {

    private static final Logger LOG = LoggerFactory.getLogger(UnprocessableEventService.class);

    private final JpaUnprocessableEventRepository repository;

    @Inject
    public UnprocessableEventService(final JpaUnprocessableEventRepository repository) {
        this.repository = repository;
    }

    public void save(UnprocessedEventEntity entity) {
        LOG.info("saving unprocessable entity "+entity);
        repository.save(entity);
    }

    public List<UnprocessedEventEntity> getAll() {
        return repository.findAll();
    }

    public List<UnprocessedEventEntity> getAllForTopic(String topic) {
        return repository.findAllByTopic(topic);
    }

}

