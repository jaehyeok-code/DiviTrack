package zerobase.divitrack.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.divitrack.persist.entity.DividendEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity,Long> {

    List<DividendEntity> findAllByCompanyId(long companyId);

    @Transactional
    void deleteAllByCompanyId(long companyId);

    Boolean existsByCompanyIdAndDate(long companyId, LocalDateTime date);
}

