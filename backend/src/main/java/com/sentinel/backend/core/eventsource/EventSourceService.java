package com.sentinel.backend.core.eventsource;
import com.sentinel.backend.core.application.*; import com.sentinel.backend.core.eventsource.dto.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service public class EventSourceService{
 private final EventSourceRepository repo; private final ApplicationRepository apps; public EventSourceService(EventSourceRepository repo,ApplicationRepository apps){this.repo=repo;this.apps=apps;}
 @Transactional public EventSourceResponse create(EventSourceRequest r){Application app=app(r.applicationId());String n=r.name().trim();if(repo.existsByNameIgnoreCaseAndApplicationId(n,app.getId()))throw new IllegalArgumentException("Event source already exists on this application");EventSource e=new EventSource();apply(e,r,app);return EventSourceResponse.from(repo.save(e));}
 @Transactional(readOnly=true) public List<EventSourceResponse> all(){return repo.findAll().stream().map(EventSourceResponse::from).toList();}
 @Transactional(readOnly=true) public EventSourceResponse one(UUID id){return EventSourceResponse.from(repo.findById(id).orElseThrow(()->new NoSuchElementException("Event source not found")));}
 @Transactional public EventSourceResponse update(UUID id,EventSourceRequest r){EventSource e=repo.findById(id).orElseThrow(()->new NoSuchElementException("Event source not found"));Application app=app(r.applicationId());String n=r.name().trim();if((!e.getName().equalsIgnoreCase(n)||!e.getApplication().getId().equals(app.getId()))&&repo.existsByNameIgnoreCaseAndApplicationId(n,app.getId()))throw new IllegalArgumentException("Event source already exists on this application");apply(e,r,app);return EventSourceResponse.from(repo.save(e));}
 @Transactional public void delete(UUID id){if(!repo.existsById(id))throw new NoSuchElementException("Event source not found");repo.deleteById(id);}
 private Application app(UUID id){return apps.findById(id).orElseThrow(()->new NoSuchElementException("Application not found"));}
 private void apply(EventSource e,EventSourceRequest r,Application app){e.setName(r.name().trim());e.setType(r.type());e.setApplication(app);if(r.active()!=null)e.setActive(r.active());}
}
