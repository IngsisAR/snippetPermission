package austral.ingsisAR.snippetPermission.shared.baseModel

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import lombok.Getter
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Getter
@MappedSuperclass
abstract class BaseModel(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,
    @Column(updatable = false)
    @CreationTimestamp
    val createdAt: LocalDateTime? = null,
    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null,
)
