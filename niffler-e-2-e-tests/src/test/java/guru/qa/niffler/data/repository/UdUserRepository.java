package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UdUserRepository {

    UdUserEntity create(UdUserEntity user);
    Optional<UdUserEntity> findById(UUID id);
    Optional<UdUserEntity> findByUsername(String username);
    void delete(UUID id);
    void addInvitation(UdUserEntity requester, UdUserEntity addressee);
    void addFriend(UdUserEntity requester, UdUserEntity addressee);

}
