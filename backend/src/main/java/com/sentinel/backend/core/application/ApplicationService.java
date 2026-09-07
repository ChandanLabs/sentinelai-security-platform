package com.sentinel.backend.core.application;
import com.sentinel.backend.core.asset.*; import com.sentinel.backend.core.application.dto.*; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.util.*;
@Service public class ApplicationService{
 private final ApplicationRepository repo; private final AssetRepository assets;
 public ApplicationService(ApplicationRepository repo,AssetRepository assets){this.repo=repo;this.assets=assets;}
 @Transactional public ApplicationResponse create(ApplicationRequest r){Asset asset=asset(r.assetId());String name=r.name().trim();if(repo.existsByNameIgnoreCaseAndAssetId(name,asset.getId()))throw new IllegalArgumentException("Application already exists on this asset");Application a=new Application();apply(a,r,asset);return ApplicationResponse.from(repo.save(a));}
 @Transactional(readOnly=true) public List<ApplicationResponse> all(){return repo.findAll().stream().map(ApplicationResponse::from).toList();}
 @Transactional(readOnly=true) public ApplicationResponse one(UUID id){return ApplicationResponse.from(repo.findById(id).orElseThrow(()->new NoSuchElementException("Application not found")));}
 @Transactional public ApplicationResponse update(UUID id,ApplicationRequest r){Application a=repo.findById(id).orElseThrow(()->new NoSuchElementException("Application not found"));Asset asset=asset(r.assetId());String name=r.name().trim();if((!a.getName().equalsIgnoreCase(name)||!a.getAsset().getId().equals(asset.getId()))&&repo.existsByNameIgnoreCaseAndAssetId(name,asset.getId()))throw new IllegalArgumentException("Application already exists on this asset");apply(a,r,asset);return ApplicationResponse.from(repo.save(a));}
 @Transactional public void delete(UUID id){if(!repo.existsById(id))throw new NoSuchElementException("Application not found");repo.deleteById(id);}
 private Asset asset(UUID id){return assets.findById(id).orElseThrow(()->new NoSuchElementException("Asset not found"));}
 private void apply(Application a,ApplicationRequest r,Asset asset){a.setName(r.name().trim());a.setVersion(clean(r.version()));a.setEnvironment(clean(r.environment()));a.setAsset(asset);if(r.active()!=null)a.setActive(r.active());}
 private String clean(String s){return s==null||s.isBlank()?null:s.trim();}
}
