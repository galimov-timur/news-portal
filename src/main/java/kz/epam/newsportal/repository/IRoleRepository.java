package kz.epam.newsportal.repository;

import kz.epam.newsportal.model.Role;
import java.util.List;

public interface IRoleRepository {
    List<Role> findRolesByUserId(long userId);
    long save(Role newRole);
    Role find(long id);
}
