package com.pcstore.infrastructure.database;
import com.pcstore.domain.model.*; import com.pcstore.repository.ProductRepository;
import java.sql.*; import java.math.BigDecimal; import java.util.*;
public final class SqliteProductRepository implements ProductRepository {
 public List<Product> findAll(){List<Product> out=new ArrayList<>();try(Connection c=Database.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT * FROM products ORDER BY category,name")){while(r.next())out.add(map(r));return out;}catch(SQLException e){throw new IllegalStateException(e);}}
 public Optional<Product> findById(String id){try(Connection c=Database.getConnection();PreparedStatement p=c.prepareStatement("SELECT * FROM products WHERE id=?")){p.setString(1,id);try(ResultSet r=p.executeQuery()){return r.next()?Optional.of(map(r)):Optional.empty();}}catch(SQLException e){throw new IllegalStateException(e);}}
 public void save(Product p){String a=null,b=null,d=null;
  if(p instanceof Cpu x)a=x.getSocket();else if(p instanceof Gpu x)a=String.valueOf(x.getWattage());else if(p instanceof Motherboard x){a=x.getSocket();b=x.getRamType();d=x.getFormFactor();}else if(p instanceof Ram x){a=x.getType();b=String.valueOf(x.getCapacityGb());}else if(p instanceof Storage x){a=String.valueOf(x.getCapacityGb());b=x.getInterfaceType();}else if(p instanceof PowerSupply x)a=String.valueOf(x.getWattage());else if(p instanceof PcCase x)a=x.getFormFactor();else if(p instanceof Cooler x)a=x.getSocket();
  try(Connection c=Database.getConnection();PreparedStatement q=c.prepareStatement("INSERT OR REPLACE INTO products VALUES(?,?,?,?,?,?,?,?)")){q.setString(1,p.getId());q.setString(2,p.getName());q.setString(3,p.getCategory().name());q.setString(4,p.getPrice().toPlainString());q.setInt(5,p.getStock());q.setString(6,a);q.setString(7,b);q.setString(8,d);q.executeUpdate();}catch(SQLException e){throw new IllegalStateException(e);}
 }

 private Product map(ResultSet r)throws SQLException{
  String id=r.getString("id"),n=r.getString("name");BigDecimal p=new BigDecimal(r.getString("price"));int st=r.getInt("stock");String a=r.getString("spec1"),b=r.getString("spec2"),d=r.getString("spec3");
  return switch(ProductCategory.valueOf(r.getString("category"))){
   case CPU->new Cpu(id,n,p,st,a);case GPU->new Gpu(id,n,p,st,Integer.parseInt(a));case MOTHERBOARD->new Motherboard(id,n,p,st,a,b,d);case RAM->new Ram(id,n,p,st,a,Integer.parseInt(b));case STORAGE->new Storage(id,n,p,st,Integer.parseInt(a),b);case POWER_SUPPLY->new PowerSupply(id,n,p,st,Integer.parseInt(a));case CASE->new PcCase(id,n,p,st,a);case COOLER->new Cooler(id,n,p,st,a);
  };
 }
}
