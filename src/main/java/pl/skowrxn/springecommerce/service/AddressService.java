package pl.skowrxn.springecommerce.service;

import pl.skowrxn.springecommerce.dto.AddressDTO;
import pl.skowrxn.springecommerce.dto.AddressListResponse;

import java.util.UUID;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO, Long userId);

    AddressDTO updateAddress(UUID addressId, AddressDTO addressDTO);

    AddressListResponse getAllAddresses(Long userId);

    void deleteAddress(UUID addressId);

    AddressDTO setDefaultAddress(Long userId, UUID addressId);

    AddressDTO getDefaultAddress(Long userId);

}
