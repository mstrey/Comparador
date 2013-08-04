<?PHP
	
include_once 'connect.php';
	
$codbarras = $_GET["codbarras"];
$descricao = ucwords(strtolower($_GET["descricao"]));

if (!empty($codbarras)){
	if (!empty($descricao)){
		$query =  " SELECT produto_id ";
		$query .= " FROM  produtos ";
		$query .= " WHERE produto_id = $codbarras";
		
		$resultSet = mysql_query($query);
		$produtos = mysql_fetch_array($resultSet);
			
		if ($produtos["produto_id"]){
			$query =  "	UPDATE produtos ";
			$query .= " SET descricao = '$descricao' ";
			$query .= " WHERE produto_id = $codbarras ";
		} else {
			$query =  "	INSERT INTO produtos ";
			$query .= " VALUES( ";
			$query .= " 	$codbarras, ";
			$query .= " 	'$descricao')";
		}
	
		mysql_query($query);
	
		$query =  " SELECT produto_id ";
		$query .= " FROM  produtos ";
		$query .= " WHERE produto_id = $codbarras";
		
		$resultSet = mysql_query($query);
		$produtos = mysql_fetch_array($resultSet);
		
		$saida = $produtos["produto_id"];
		$saida .= "\n";
				
	} else {
		$query =  " select * ";
		$query .= " from  produtos ";
		$query .= " where produto_id = $codbarras";
	
		$resultSet = mysql_query($query);
		$dados = mysql_fetch_array($resultSet);
	
		if ($dados["produto_id"]){
			$saida = $dados["produto_id"];
			$saida .= ">";
			$saida .= $dados["descricao"];
			$saida .= "\n";
			
			echo $saida;
		} else {
			echo 'p2';
		}
	}	
} else {
	echo 'p3';
}
	
?>

