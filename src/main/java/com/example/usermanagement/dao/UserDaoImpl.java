package com.example.usermanagement.dao;

import static com.example.usermanagement.jooq.Tables.TB_USER;

import com.example.usermanagement.jooq.tables.records.TbUserRecord;
import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserDaoImpl extends DAOImpl<TbUserRecord, TbUserRecord, String> implements UserDao {


    public UserDaoImpl(Configuration configuration) {
        super(TB_USER, TbUserRecord.class, configuration);
    }

    @Override
    public void createUser(String name, String password, String email, String role) {
        super.insert(new TbUserRecord(name, password, email, role));
    }

    @Override
    public void deleteUser(String name) {
        super.deleteById(name);
    }

    @Override
    public void updateUser(String name, String password, String email, String role) {
        super.update(new TbUserRecord(name, password, email, role));
    }

    @Override
    public TbUserRecord getUser(String name) {
        return super.findById(name);
    }

    @Override
    public List<TbUserRecord> getAllUsers() {
        return super.findAll();
    }

    @Override
    public String getId(TbUserRecord userRecord) {
        return userRecord.getUsername();
    }
}
