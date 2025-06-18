package pl.skowrxn.springecommerce.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import pl.skowrxn.springecommerce.dto.AddressDTO;
import pl.skowrxn.springecommerce.dto.response.AddressListResponse;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.service.AddressService;
import pl.skowrxn.springecommerce.util.AuthUtil;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @Mock
    private AuthUtil authUtil;

    @InjectMocks
    private AddressController addressController;

    private User mockUser;

    @BeforeEach
    void setUp(){
        mockUser = new User();
        mockUser.setId(1L);
    }

    @Test
    void createAddress_ShouldReturnCreatedAddress() {
        AddressDTO dto = new AddressDTO();
        dto.setId(UUID.randomUUID());

        when(authUtil.getLoggedInUser()).thenReturn(mockUser);
        when(addressService.createAddress(any(AddressDTO.class), eq(1L))).thenReturn(dto);

        ResponseEntity<AddressDTO> response = addressController.createAddress(dto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void updateAddress_ShouldReturnUpdatedAddress() {
        UUID id = UUID.randomUUID();
        AddressDTO dto = new AddressDTO();

        when(addressService.updateAddress(eq(id), any(AddressDTO.class))).thenReturn(dto);

        ResponseEntity<AddressDTO> response = addressController.updateAddress(id, dto);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getAllAddresses_ShouldReturnAddressList() {
        AddressListResponse responseDto = new AddressListResponse();
        responseDto.setUserId(1L);
        responseDto.setAddresses(Collections.emptyList());

        when(authUtil.getLoggedInUser()).thenReturn(mockUser);
        when(addressService.getAllAddresses(1L)).thenReturn(responseDto);

        ResponseEntity<AddressListResponse> response = addressController.getAllAddresses();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());
    }

    @Test
    void deleteAddress_ShouldReturnNoContent() {
        UUID id = UUID.randomUUID();

        ResponseEntity<Void> response = addressController.deleteAddress(id);

        verify(addressService).deleteAddress(id);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void setDefaultAddress_ShouldReturnUpdatedAddress() {
        UUID addressId = UUID.randomUUID();
        AddressDTO dto = new AddressDTO();

        when(authUtil.getLoggedInUser()).thenReturn(mockUser);
        when(addressService.setDefaultAddress(1L, addressId)).thenReturn(dto);

        ResponseEntity<AddressDTO> response = addressController.setDefaultAddress(addressId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getDefaultAddress_ShouldReturnDefaultAddress() {
        AddressDTO dto = new AddressDTO();

        when(authUtil.getLoggedInUser()).thenReturn(mockUser);
        when(addressService.getDefaultAddress(1L)).thenReturn(dto);

        ResponseEntity<AddressDTO> response = addressController.getDefaultAddress();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }
}
