package pl.pjatk.Stepify.user.mapper;

import org.mapstruct.Mapper;
import pl.pjatk.Stepify.user.dto.AddressDTO;
import pl.pjatk.Stepify.user.model.Address;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDTO mapAddressToAddressDTO(Address address);
    Address mapAddressDTOToAddress(AddressDTO addressDTO);
    List<AddressDTO> mapListAddressToAddressDTO(List<Address> addresses);
}
