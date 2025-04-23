package pl.skowrxn.springecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import pl.skowrxn.springecommerce.dto.AddressDTO;
import pl.skowrxn.springecommerce.dto.AddressListResponse;
import pl.skowrxn.springecommerce.entity.Address;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.AddressRepository;
import pl.skowrxn.springecommerce.repository.UserRepository;

import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        addressDTO.setDefaultAddress(user.getAddresses().isEmpty());

        Address address = this.modelMapper.map(addressDTO, Address.class);
        address.setUser(user);
        user.getAddresses().add(address);

        Address savedAddress = this.addressRepository.save(address);
        return this.modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public AddressDTO updateAddress(UUID addressId, AddressDTO addressDTO) {
        Address address = this.addressRepository.findById(addressId).orElseThrow(
                () -> new ResourceNotFoundException("Address", "id", addressId));
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setProvince(addressDTO.getProvince());
        address.setCountry(addressDTO.getCountry());
        address.setPostalCode(addressDTO.getPostalCode());
        address.setPhoneNumber(addressDTO.getPhoneNumber());
        address.setDefaultAddress(addressDTO.isDefaultAddress());
        if(addressDTO.isDefaultAddress()) {
            address.getUser().getAddresses().forEach(addr -> addr.setDefaultAddress(false));
            address.setDefaultAddress(true);
        }
        Address updatedAddress = this.addressRepository.save(address);
        return this.modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public AddressListResponse getAllAddresses(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        AddressListResponse addressListResponse = new AddressListResponse();
        addressListResponse.setTotalAddresses(user.getAddresses().size());
        addressListResponse.setUserId(user.getId());
        addressListResponse.setAddresses(user.getAddresses().stream()
                .map(address -> this.modelMapper.map(address, AddressDTO.class))
                .toList());
        return addressListResponse;
    }

    @Override
    public void deleteAddress(UUID addressId) {
        Address address = this.addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        if (address.isDefaultAddress()) {
            User user = address.getUser();
            user.getAddresses().remove(address);
            if (!user.getAddresses().isEmpty()) {
                Address newDefaultAddress = user.getAddresses().stream()
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Address", "userId", user.getId()));
                newDefaultAddress.setDefaultAddress(true);
            }
            this.userRepository.save(user);
        } else {
            this.addressRepository.delete(address);
        }
    }

    @Override
    public AddressDTO getDefaultAddress(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getAddresses().isEmpty()) {
            throw new ResourceNotFoundException("Address", "userId", userId);
        }
        Address defaultAddress = user.getAddresses().stream()
                .filter(Address::isDefaultAddress)
                .findFirst()
                .orElse(user.getAddresses().stream().findFirst().orElseThrow(
                        () -> new ResourceNotFoundException("Address", "userId", userId)));

        return this.modelMapper.map(defaultAddress, AddressDTO.class);
    }

    @Override
    public AddressDTO setDefaultAddress(Long userId, UUID addressId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Address address = this.addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        if (!address.getUser().getId().equals(userId) || user.getAddresses().isEmpty()) {
            throw new ResourceNotFoundException("Address", "userId", userId);
        }

        user.getAddresses().forEach(addr -> addr.setDefaultAddress(false));
        address.setDefaultAddress(true);
        this.userRepository.save(user);
        Address updatedAddress = user.getAddresses().stream()
                .filter(Address::isDefaultAddress)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Address", "userId", user.getId()));

        return this.modelMapper.map(updatedAddress, AddressDTO.class);
    }
}
