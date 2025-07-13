package com.minisocial.dto;
import com.minisocial.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    
    @NotBlank
    @Size(min = 2, max = 50)
    @JsonProperty(value = "username", required = false)
    private String name;
    
    @JsonProperty(value = "firstName", required = false)
    private String firstName;
    
    @JsonProperty(value = "lastName", required = false)
    private String lastName;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
    
    private String bio;
    
    private User.UserRole role;


    public UserDTO() {
    }


    public UserDTO(UserDTO user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.role = user.getRole();
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.bio = user.getBio();
        this.role = user.getRole();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        if (name == null && firstName != null) {
            return firstName + (lastName != null ? " " + lastName : "");
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        if (name == null && firstName != null) {
            this.name = firstName + (lastName != null ? " " + lastName : "");
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        if (name == null && firstName != null) {
            this.name = firstName + " " + lastName;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public User.UserRole getRole() {
        return role;
    }

    public void setRole(User.UserRole role) {
        this.role = role;
    }
} 