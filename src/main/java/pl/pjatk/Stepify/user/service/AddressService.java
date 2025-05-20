package pl.pjatk.Stepify.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.user.dto.AddressDTO;
import pl.pjatk.Stepify.user.mapper.AddressMapper;
import pl.pjatk.Stepify.user.model.Address;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.repository.AddressRepository;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserService userService;

    public AddressDTO addAddress(AddressDTO addressDTO) {
        User user = userService.getCurrentUser();
        Address address = addressMapper.mapAddressDTOToAddress(addressDTO);
        address.setUser(user);
        return addressMapper.mapAddressToAddressDTO(addressRepository.save(address));
    }
}
