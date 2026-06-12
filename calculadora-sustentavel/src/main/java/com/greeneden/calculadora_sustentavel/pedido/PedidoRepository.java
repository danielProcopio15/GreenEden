package com.greeneden.calculadora_sustentavel.pedido;

import com.greeneden.calculadora_sustentavel.pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    // Busca pedidos órfãos pelo email de contato (para vincular após criar conta)
    List<Pedido> findByEmailContatoAndUsuarioIsNull(String emailContato);

    // Pedidos ativos do usuário
    List<Pedido> findByUsuarioIdAndStatusOrderByCriadoEmDesc(Long usuarioId, String status);

    // Pedidos cancelados do usuário
    @Query("SELECT p FROM Pedido p WHERE p.usuario.id = :usuarioId AND p.status != 'Ativo' ORDER BY p.criadoEm DESC")
    List<Pedido> findHistoricoPorUsuario(@Param("usuarioId") Long usuarioId);

    // Vincula pedidos órfãos a um usuário
    @Modifying
    @Query("UPDATE Pedido p SET p.usuario = (SELECT u FROM Usuario u WHERE u.id = :usuarioId) WHERE p.emailContato = :email AND p.usuario IS NULL")
    int vincularPedidosOrfaos(@Param("usuarioId") Long usuarioId, @Param("email") String email);
}