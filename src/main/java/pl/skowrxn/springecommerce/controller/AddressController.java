package pl.skowrxn.springecommerce.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skowrxn.springecommerce.dto.AddressDTO;
import pl.skowrxn.springecommerce.dto.response.AddressListResponse;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.service.AddressService;
import pl.skowrxn.springecommerce.util.AuthUtil;

import java.net.URI;
import java.util.UUID;

@RestController
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    public AddressController(AddressService addressService, AuthUtil authUtil) {
        this.addressService = addressService;
        this.authUtil = authUtil;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@RequestBody @Valid AddressDTO addressDTO) {
        User user = this.authUtil.getLoggedInUser();
        AddressDTO address = this.addressService.createAddress(addressDTO, user.getId());
        return ResponseEntity.created(URI.create("/addresses/" + address.getId()))
                .body(address);
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable UUID id,
                                                    @RequestBody @Valid AddressDTO addressDTO) {
        AddressDTO updatedAddress = this.addressService.updateAddress(id, addressDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @GetMapping("/addresses")
    public ResponseEntity<AddressListResponse> getAllAddresses() {
        User user = this.authUtil.getLoggedInUser();
        return ResponseEntity.ok(this.addressService.getAllAddresses(user.getId()));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        this.addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/addresses/default/{id}")
    public ResponseEntity<AddressDTO> setDefaultAddress(@PathVariable UUID id) {
        User user = this.authUtil.getLoggedInUser();
        AddressDTO address = this.addressService.setDefaultAddress(user.getId(), id);
        return ResponseEntity.ok(address);
    }

    @GetMapping("/addresses/default")
    public ResponseEntity<AddressDTO> getDefaultAddress() {
        User user = this.authUtil.getLoggedInUser();
        AddressDTO address = this.addressService.getDefaultAddress(user.getId());
        return ResponseEntity.ok(address);
    }



}
