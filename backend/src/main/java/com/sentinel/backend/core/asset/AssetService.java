package com.sentinel.backend.core.asset;
import com.sentinel.backend.core.asset.dto.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class AssetService {
 private final AssetRepository repo;
 public AssetService(AssetRepository repo){this.repo=repo;}
 @Transactional public AssetResponse create(AssetRequest r){ if(repo.existsByNameIgnoreCase(r.name().trim())) throw new IllegalArgumentException("Asset name already exists"); Asset a=new Asset(); apply(a,r); return AssetResponse.from(repo.save(a)); }
 @Transactional(readOnly=true) public List<AssetResponse> findAll(){return repo.findAll().stream().map(AssetResponse::from).toList();}
 @Transactional(readOnly=true) public AssetResponse findById(UUID id){return AssetResponse.from(repo.findById(id).orElseThrow(()->new NoSuchElementException("Asset not found")));}
 @Transactional public AssetResponse update(UUID id, AssetRequest r){Asset a=repo.findById(id).orElseThrow(()->new NoSuchElementException("Asset not found")); String name=r.name().trim(); if(!a.getName().equalsIgnoreCase(name)&&repo.existsByNameIgnoreCase(name)) throw new IllegalArgumentException("Asset name already exists"); apply(a,r); return AssetResponse.from(repo.save(a));}
 @Transactional public void delete(UUID id){if(!repo.existsById(id)) throw new NoSuchElementException("Asset not found"); repo.deleteById(id);}
 private void apply(Asset a,AssetRequest r){a.setName(r.name().trim());a.setHostname(clean(r.hostname()));a.setIpAddress(clean(r.ipAddress()));a.setEnvironment(r.environment().trim().toLowerCase(Locale.ROOT));if(r.active()!=null)a.setActive(r.active());}
 private String clean(String s){return s==null||s.isBlank()?null:s.trim();}
}
