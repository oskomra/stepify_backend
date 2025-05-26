package pl.pjatk.Stepify.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pjatk.Stepify.exception.ResourceNotFoundException;
import pl.pjatk.Stepify.exception.UnauthorizedAccessException;
import pl.pjatk.Stepify.user.dto.AddressDTO;
import pl.pjatk.Stepify.user.mapper.AddressMapper;
import pl.pjatk.Stepify.user.model.Address;
import pl.pjatk.Stepify.user.model.User;
import pl.pjatk.Stepify.user.repository.AddressRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserService userService;

    public List<AddressDTO> getAddresses(){
        if(addressRepository.findAll().isEmpty()){
            throw new ResourceNotFoundException("No address found");
        } else {
            return addressRepository.findAll()
                    .stream()
                    .map(addressMapper::mapAddressToAddressDTO)
                    .collect(Collectors.toList());
        }

    }

    public AddressDTO addAddress(AddressDTO addressDTO) {
        User user = userService.getCurrentUser();
        Address address = addressMapper.mapAddressDTOToAddress(addressDTO);
        address.setUser(user);
        return addressMapper.mapAddressToAddressDTO(addressRepository.save(address));
    }

    public AddressDTO updateAddress(long id, AddressDTO addressDTO) {
        User user = userService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (address.getUser().getId() != user.getId()) {
            throw new UnauthorizedAccessException("You do not have permission to update this address");
        } else {
            address.setStreet(addressDTO.getStreet());
            address.setCity(addressDTO.getCity());
            address.setCountry(addressDTO.getCountry());
            address.setPostalCode(addressDTO.getPostalCode());
            return addressMapper.mapAddressToAddressDTO(addressRepository.save(address));
        }
    }

    public void deleteAddress(long id) {
        User user = userService.getCurrentUser();
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().equals(user)) {
            throw new UnauthorizedAccessException("You do not have permission to delete this address");
        }
        addressRepository.delete(address);
    }
}
