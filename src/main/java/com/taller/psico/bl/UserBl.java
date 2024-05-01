package com.taller.psico.bl;

import com.taller.psico.dto.PaginatedResponseDTO;
import com.taller.psico.dto.PeopleDTO;
import com.taller.psico.dto.UseriDTO;
import com.taller.psico.entity.People;
import com.taller.psico.entity.Useri;
import com.taller.psico.repository.PeopleRepository;
import com.taller.psico.repository.UseriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserBl {

    @Autowired
    private UseriRepository userRepository;
    @Autowired
    private PeopleRepository peopleRepository;

    // Mostrar usuario por id incluyendo detalles de persona
    public UseriDTO findByIdUser(Integer userId){
        Useri useri = userRepository.findByIdUser(userId);
        People people = useri.getPeopleId();  // Asume que getPeopleId() devuelve un objeto People
        PeopleDTO peopleDTO = convertToPeopleDTO(people);  // Convierte People a PeopleDTO

        UseriDTO useriDTO = new UseriDTO();
        useriDTO.setUserId(useri.getUserId());
        useriDTO.setUserName(useri.getUserName());
        useriDTO.setStatus(useri.getStatus());
        useriDTO.setPeople(peopleDTO);  // Asigna el objeto PeopleDTO completo
        useriDTO.setRolId(useri.getRolId().getRolId());
        return useriDTO;
    }

    //Actualizar persona de un usuario
    public void updateUser(PeopleDTO peopleDTO, Integer userId){
        Useri useri = userRepository.findByIdUser(userId);
        People people = useri.getPeopleId();
        people.setName(peopleDTO.getName());
        people.setFirstLastname(peopleDTO.getFirstLastname());
        people.setSecondLastname(peopleDTO.getSecondLastname());
        people.setEmail(peopleDTO.getEmail());
        people.setAge(peopleDTO.getAge());
        people.setCellphone(peopleDTO.getCellphone());
        people.setAddress(peopleDTO.getAddress());
        people.setCi(peopleDTO.getCi());
        userRepository.save(useri);
    }


    //Mostrar persona por id de usuario
    public PeopleDTO findByIdPerson(Integer userId){
        PeopleDTO peopleDTO = new PeopleDTO();
        Useri useri = userRepository.findByIdUser(userId);
        People people = useri.getPeopleId();
        peopleDTO.setPeopleId(people.getPeopleId());
        peopleDTO.setName(people.getName());
        peopleDTO.setFirstLastname(people.getFirstLastname());
        peopleDTO.setSecondLastname(people.getSecondLastname());
        peopleDTO.setEmail(people.getEmail());
        peopleDTO.setAge(people.getAge());
        peopleDTO.setCellphone(people.getCellphone());
        peopleDTO.setAddress(people.getAddress());
        peopleDTO.setCi(people.getCi());
        return peopleDTO;
    }

    public boolean deleteUser(Integer userId) {
        Useri user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setStatus(false);  // Assuming there's a 'status' field to indicate activity
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public List<UseriDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUseriDTO)
                .collect(Collectors.toList());
    }

    public List<UseriDTO> findUsersByRoleId(Integer rolId) {
        return userRepository.findByRolId(rolId).stream()
                .map(this::convertToUseriDTO)
                .collect(Collectors.toList());
    }

    public List<PeopleDTO> findPeopleByRoleId(Integer rolId) {
        List<People> allPeople = peopleRepository.findPeopleByRoleId(rolId);
        return allPeople.stream()
                .map(this::convertToPeopleDTO)
                .collect(Collectors.toList());
    }



    private UseriDTO convertToUseriDTO(Useri user) {
        // Crear instancia de UseriDTO
        UseriDTO dto = new UseriDTO();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUserName());
        dto.setStatus(user.getStatus());
        dto.setRolId(user.getRolId().getRolId());

        // Convertir People a PeopleDTO si People no es null
        if (user.getPeopleId() != null) {
            PeopleDTO peopleDTO = convertToPeopleDTO(user.getPeopleId());
            dto.setPeople(peopleDTO);
        }

        return dto;
    }

    private PeopleDTO convertToPeopleDTO(People people) {
        PeopleDTO dto = new PeopleDTO();
        dto.setPeopleId(people.getPeopleId());
        dto.setName(people.getName());
        dto.setFirstLastname(people.getFirstLastname());
        dto.setSecondLastname(people.getSecondLastname());
        dto.setEmail(people.getEmail());
        dto.setAge(people.getAge());
        dto.setCellphone(people.getCellphone());
        dto.setAddress(people.getAddress());
        dto.setCi(people.getCi());
        dto.setStatus(people.getStatus());
        return dto;
    }



}
