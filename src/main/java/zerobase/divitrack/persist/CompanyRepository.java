package zerobase.divitrack.persist;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.divitrack.persist.entity.CompanyEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    Boolean existsByTicker(String ticker);

    Optional<CompanyEntity> findByName(String name);

    // Like 연산자를 사용하여 자동완성기능 구현하기 위한 메서드
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String s, Pageable pageable);
}
