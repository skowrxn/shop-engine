package pl.skowrxn.springecommerce.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import pl.skowrxn.springecommerce.dto.AddressDTO;
import pl.skowrxn.springecommerce.dto.response.AddressListResponse;
import pl.skowrxn.springecommerce.entity.Address;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.AddressRepository;
import pl.skowrxn.springecommerce.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void testCreateAddress_shouldCreateAndReturnAddressDTO() {
        AddressDTO inputDto = new AddressDTO();
        inputDto.setStreet("Main Street");
        inputDto.setCity("Cracow");
        inputDto.setProvince("Province");
        inputDto.setCountry("Country");
        inputDto.setPostalCode("12345");
        inputDto.setPhoneNumber("555-1234");

        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setAddresses(new ArrayList<>());

        AddressDTO inputDTO = new AddressDTO();
        inputDTO.setCity("Cracow");
        inputDTO.setStreet("Main Street");

        Address mappedAddress = new Address();
        mappedAddress.setCity("Cracow");
        mappedAddress.setStreet("Main Street");

        Address savedAddress = new Address();
        savedAddress.setCity("Cracow");
        savedAddress.setStreet("Main Street");

        AddressDTO outputDTO = new AddressDTO();
        outputDTO.setCity("Cracow");
        outputDTO.setStreet("Main Street");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(inputDto, Address.class)).thenReturn(mappedAddress);
        when(addressRepository.save(mappedAddress)).thenReturn(savedAddress);
        when(modelMapper.map(savedAddress, AddressDTO.class)).thenReturn(outputDTO);

        AddressDTO result = addressService.createAddress(inputDto, userId);

        assertNotNull(result);
        assertEquals("Main Street", result.getStreet());
        assertEquals("Cracow", result.getCity());

        assertTrue(inputDto.isDefaultAddress(), "Pierwszy adres powinien być defaultAddress=true");

        verify(userRepository).findById(userId);
        verify(addressRepository).save(mappedAddress);
        verify(modelMapper).map(inputDto, Address.class);
        verify(modelMapper).map(savedAddress, AddressDTO.class);
    }

    @Test
    void testCreateAddress_userNotFound() {
        Long userId = 999L;
        AddressDTO inputDto = new AddressDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.createAddress(inputDto, userId));

        verify(userRepository).findById(userId);
        verifyNoInteractions(modelMapper);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testGetAllAddresses_userNotFound(){
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getAllAddresses(userId));

        verify(userRepository).findById(userId);
        verifyNoInteractions(modelMapper);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testGetAllAddresses_userExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Address address1 = new Address();
        Address address2 = new Address();
        user.setAddresses(Arrays.asList(address1, address2));

        AddressDTO addressDTO1 = new AddressDTO();
        AddressDTO addressDTO2 = new AddressDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(address1, AddressDTO.class)).thenReturn(addressDTO1);
        when(modelMapper.map(address2, AddressDTO.class)).thenReturn(addressDTO2);

        AddressListResponse result = addressService.getAllAddresses(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getTotalAddresses());

        verify(userRepository).findById(userId);
        verify(modelMapper).map(address1, AddressDTO.class);
        verify(modelMapper).map(address2, AddressDTO.class);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testGetDefaultAddress_userExists_defaultIsSet() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        UUID id1 = UUID.randomUUID();
        Address defaultAddress = new Address();
        defaultAddress.setDefaultAddress(true);

        Address otherAddress = new Address();
        otherAddress.setDefaultAddress(false);

        user.setAddresses(Arrays.asList(defaultAddress, otherAddress));

        AddressDTO expectedDTO = new AddressDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(defaultAddress, AddressDTO.class)).thenReturn(expectedDTO);

        AddressDTO result = addressService.getDefaultAddress(id);

        assertNotNull(result);
        assertSame(expectedDTO, result);

        verify(userRepository).findById(id);
        verify(modelMapper).map(defaultAddress, AddressDTO.class);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testGetDefaultAddress_userExists_noDefaultAddress() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        UUID id1 = UUID.randomUUID();
        Address address1 = new Address();
        address1.setDefaultAddress(false);

        Address address2 = new Address();
        address2.setDefaultAddress(false);

        user.setAddresses(Arrays.asList(address1, address2));

        AddressDTO expectedDTO = new AddressDTO();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(address1, AddressDTO.class)).thenReturn(expectedDTO);

        AddressDTO result = addressService.getDefaultAddress(id);

        assertNotNull(result);
        assertSame(expectedDTO, result);

        verify(userRepository).findById(id);
        verify(modelMapper).map(address1, AddressDTO.class);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testGetDefaultAddress_userNotExists() {
        Long id = 999L;
        User user = new User();
        user.setId(id);

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> addressService.getDefaultAddress(id));

        verify(userRepository).findById(id);
        verifyNoInteractions(modelMapper);
        verifyNoInteractions(addressRepository);
    }

    @Test
    void testSetDefaultAddress_userExists() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        UUID oldId = UUID.randomUUID();
        Address oldAddress = new Address();
        oldAddress.setId(oldId);
        oldAddress.setUser(user);
        oldAddress.setDefaultAddress(true);

        UUID newId = UUID.randomUUID();
        Address newAddress = new Address();
        newAddress.setId(newId);
        newAddress.setUser(user);
        newAddress.setDefaultAddress(false);

        AddressDTO expectedDTO = new AddressDTO();
        expectedDTO.setId(newId);
        expectedDTO.setDefaultAddress(true);

        user.setAddresses(Arrays.asList(newAddress, oldAddress));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(modelMapper.map(newAddress, AddressDTO.class)).thenReturn(expectedDTO);
        when(addressRepository.findById(newId)).thenReturn(Optional.of(newAddress));

        AddressDTO result = addressService.setDefaultAddress(id, newId);

        assertNotNull(result);

        assertTrue(result.isDefaultAddress());
        assertSame(result, expectedDTO);

        Address updatedNew = user.getAddresses().stream().filter(address -> address.getId().equals(newId)).findFirst().get();
        Address updatedOld = user.getAddresses().stream().filter(address -> address.getId().equals(oldId)).findFirst().get();

        assertTrue(updatedNew.isDefaultAddress());
        assertFalse(updatedOld.isDefaultAddress());

        verify(userRepository).findById(id);
        verify(addressRepository).findById(newId);
        verify(userRepository).save(user);
        verify(modelMapper).map(newAddress, AddressDTO.class);
    }

}
