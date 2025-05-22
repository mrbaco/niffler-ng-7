package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import java.util.UUID;

public record UserJson(

        @Deprecated
        @JsonIgnore
        boolean error,

        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("photo")
        byte[] photo,
        @JsonProperty("photoSmall")
        byte[] photoSmall) {

        @Deprecated
        public UserJson(UUID id, String username, String firstname, String surname, String fullname, CurrencyValues currency, byte[] photo, byte[] photoSmall) {
                this(false, id, username, firstname, surname, fullname, currency, photo, photoSmall);
        }

        public UdUserEntity toUdUserEntity() {
                UdUserEntity udUserEntity = new UdUserEntity();

                udUserEntity.setId(this.id);
                udUserEntity.setUsername(this.username);
                udUserEntity.setFirstname(this.firstname);
                udUserEntity.setSurname(this.surname);
                udUserEntity.setFullname(this.fullname);
                udUserEntity.setCurrency(this.currency);
                udUserEntity.setPhoto(this.photo);
                udUserEntity.setPhotoSmall(this.photoSmall);

                return udUserEntity;
        }

}
