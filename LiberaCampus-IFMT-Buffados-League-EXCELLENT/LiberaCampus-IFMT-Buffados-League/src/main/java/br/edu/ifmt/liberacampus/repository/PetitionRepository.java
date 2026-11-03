package br.edu.ifmt.liberacampus.repository;

import br.edu.ifmt.liberacampus.model.Department;
import br.edu.ifmt.liberacampus.model.Petition;
import br.edu.ifmt.liberacampus.model.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PetitionRepository extends JpaRepository<Petition, Long> {

    @EntityGraph(attributePaths = {"requester", "departmentEntries"})
    List<Petition> findByRequesterOrderByCreatedAtDesc(UserAccount requester);

    @EntityGraph(attributePaths = {"requester", "departmentEntries"})
    @Query("select distinct p from Petition p join p.departmentEntries pd "
            + "where pd.department in :departments order by p.createdAt desc")
    List<Petition> findVisibleToDepartments(@Param("departments") Collection<Department> departments);

    @EntityGraph(attributePaths = {"requester", "departmentEntries"})
    @Query("select p from Petition p where p.id = :id")
    Optional<Petition> findDetailedById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"requester", "departmentEntries"})
    List<Petition> findAllByOrderByCreatedAtDesc();

    long countByRequester(UserAccount requester);
}
