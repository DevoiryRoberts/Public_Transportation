package com.example.public_transportation_project.Services;

import com.example.public_transportation_project.Models.Driver;
import com.example.public_transportation_project.Mappers.DriverMapper;
import com.example.public_transportation_project.Repositories.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import com.example.public_transportation_project.DTOs.DriverRequest;
import com.example.public_transportation_project.DTOs.DriverUpdate;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Transactional
    public Driver createDriver(DriverRequest dto) {
        if (driverRepository.existsByEmailIncludeDeleted(dto.getEmail())) {
            throw new RuntimeException("שגיאה: המייל " + dto.getEmail() + " כבר קיים במערכת");
        }
        if (driverRepository.existsByLicenseNumberIncludeDeleted(dto.getLicenseNumber())) {
            throw new RuntimeException("שגיאה: מספר הרישיון כבר רשום במערכת");
        }
        if (driverRepository.existsByPhone(dto.getPhone())) {
            throw new RuntimeException("שגיאה: מספר הטלפון כבר קיים במערכת");
        }

        Driver driver = new Driver();
        DriverMapper.updateEntityFromRequest(dto, driver);

        driver.setRole("DRIVER");
        driver.setActive(true);
        driver.setDeletionRequested(false);

        return driverRepository.save(driver);
    }

    @Transactional
    public void deleteDriver(Long id, String currentUserEmail) {
        Driver driverToDelete = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));

        if ("ADMIN".equals(driverToDelete.getRole())) {
            if (countActiveAdmins() <= 1) {
                throw new RuntimeException("שגיאה: לא ניתן למחוק את המנהל האחרון במערכת!");
            }
        }

        driverRepository.delete(driverToDelete);
    }

    @Transactional
    public Driver updateDriver(Long id, DriverRequest dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));

        driverRepository.findByEmailIncludeDeleted(dto.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("המייל הזה שייך לנהג אחר במערכת");
            }
        });

        DriverMapper.updateEntityFromRequest(dto, driver);
        return driverRepository.save(driver);
    }

    public void restoreDriver(Long id) {
        Driver driver = driverRepository.findByIdIncludeDeleted(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא בארכיון"));
        driver.setActive(true);
        driver.setDeletionRequested(false);
        driverRepository.save(driver);
    }

    @Transactional
    public void forcePermanentDelete(Long id) {
        try {
            driverRepository.hardDeleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("לא ניתן למחוק את הנהג לצמיתות מכיוון שיש לו נסיעות רשומות במערכת. יש למחוק את הנסיעות תחילה או להשתמש במחיקה רגילה (ארכיון).");
        }
    }

    public void requestPermanentDeletion(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));
        driver.setDeletionRequested(true);
        driverRepository.save(driver);
    }

    public List<Driver> getDriversRequestingDeletion() {
        return driverRepository.findByDeletionRequestedTrue();
    }

    private long countActiveAdmins() {
        return driverRepository.countByRoleAndIsActiveTrue("ADMIN");
    }

    @Transactional
    public Driver patchDriver(Long id, DriverUpdate dto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));

        if (dto.getEmail() != null) {
            if (driverRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
                throw new RuntimeException("שגיאה: האימייל כבר תפוס על ידי משתמש אחר");
            }
            driver.setEmail(dto.getEmail());
        }

        if (dto.getPhone() != null) {
            if (driverRepository.existsByPhoneAndIdNot(dto.getPhone(), id)) {
                throw new RuntimeException("שגיאה: מספר הטלפון כבר קיים במערכת");
            }
            driver.setPhone(dto.getPhone());
        }

        if (dto.getLicenseNumber() != null) {
            if (driverRepository.existsByLicenseNumberAndIdNot(dto.getLicenseNumber(), id)) {
                throw new RuntimeException("שגיאה: מספר הרישיון כבר רשום לנהג אחר");
            }
            driver.setLicenseNumber(dto.getLicenseNumber());
        }

        if (dto.getName() != null) {
            driver.setName(dto.getName());
        }

        DriverMapper.updateEntityFromPatch(dto, driver);

        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriverRoleToAdmin(Long id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("לא נמצא משתמש עם מזהה: " + id));

        driver.setRole("ADMIN");

        return driverRepository.save(driver);
    }

    public List<Driver> getTopRatedDrivers() {
        return driverRepository.findByRatingAndIsActiveTrue(5);
    }

    @Transactional
    public void updateDriverRating(Long id, int newRating) {
        if (newRating < 1 || newRating > 5) {
            throw new RuntimeException("דירוג חייב להיות בין 1 ל-5");
        }
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));
        driver.setRating(newRating);
        driverRepository.save(driver);
    }

    @Transactional
    public void addRatingToDriver(Long id, int starCount) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));

        int newAverage = (driver.getRating() + starCount) / 2;
        driver.setRating(newAverage);

        driverRepository.save(driver);

        if (newAverage < 2) {
            System.out.println("ALERT: Driver " + driver.getName() + " has dropped to critical rating!");
        }
    }

    @Transactional
    public void rateDriver(Long id, int stars) {
        if (stars < 1 || stars > 5) {
            throw new RuntimeException("דירוג חייב להיות בין 1 ל-5 כוכבים");
        }

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("נהג לא נמצא"));

        int currentRating = driver.getRating();
        int newRating = (currentRating == 0) ? stars : Math.round((currentRating + stars) / 2.0f);

        driver.setRating(newRating);
        driverRepository.save(driver);

        if (newRating < 2) {
            System.out.println("LOG: Driver " + driver.getName() + " has reached a low rating threshold!");
        }
    }

    public List<Driver> getExemplaryDrivers() {
        return driverRepository.findByRatingGreaterThanEqualAndIsActiveTrue(4);
    }

}
