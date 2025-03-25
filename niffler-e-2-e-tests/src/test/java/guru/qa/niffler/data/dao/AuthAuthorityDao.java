package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.List;
import java.util.UUID;

public interface AuthAuthorityDao {

    AuthorityEntity[] create(AuthorityEntity... authorities);
    List<AuthorityEntity> findByUserId(UUID id);
    void deleteByUserId(UUID id);
    List<AuthorityEntity> findAll();

}
