package com.example.apptive_3team.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 챙길 물품 엔티티.
 *
 * <p><구성 필드>
 * <br>Long id: 물품 id
 * <br>Long user_id: 사용자 id (User테이블 외래키)
 * <br>String name: 물품 이름 (50자 제한)
 * <br>LocalDate deadline: 물품이 필요한 날짜 (MM-DD)
 */
@Entity
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id") // DB 컬럼명은 user_id로 매핑
    private Long userId;

    private String name;
    private LocalDate deadline;

    public Item() {}

    public Item(long userId, String name, LocalDate deadline) {
        this.userId = userId;
        this.name = name;
        this.deadline = deadline;
    }
}
