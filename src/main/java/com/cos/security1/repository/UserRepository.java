package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//CRUD �Լ��� JpaRepository�� ��� ����.
//@Repository��� ������̼��� ��� IOC�ȴ�. ������ JpaRepository�� ����߱� ����.
public interface UserRepository extends JpaRepository<User, Integer>{
	
	// findBy?(String ?) ��Ģ
	// findBy(select * from user where ? = ?)
	public User findByUsername(String username);
}
