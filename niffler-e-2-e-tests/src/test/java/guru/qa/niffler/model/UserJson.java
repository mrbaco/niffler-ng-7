package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;

import java.util.UUID;

public record UserJson(

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
