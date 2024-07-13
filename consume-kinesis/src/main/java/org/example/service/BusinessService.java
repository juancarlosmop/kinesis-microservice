package org.example.service;

import org.example.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class BusinessService {

    public void validAge(UserDTO user){
        if(user.getAge()>=1 && user.getAge()<=10){
            System.out.println("Usted es un  niñp");
        }else if(user.getAge()>10 && user.getAge()<18){
            System.out.println("Usted es un  adolescente");
        }else if(user.getAge()>18 && user.getAge()<60){
            System.out.println("Usted es un  adulto");
        }else {
            System.out.println("Usted esta viejo");
        }
    }
}
