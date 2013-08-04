<?PHP
	
include_once 'connect.php';
	
$nome = ucwords(strtolower($_GET["nome"]));
$local = ucwords(strtolower($_GET["local"]));
$cidade = $_GET["cidade"];
$loja_id = $_GET["loja_id"];
$modo = '0';
$separador = ">";

if (!empty($nome) and !empty($local) and !empty($cidade)) {
	$modo = 1; // incluir
}
if (!empty($loja_id) and !empty($nome) and !empty($local) and !empty($cidade)) {
	$modo = 2; // atualizar
};

switch ($modo) {
	case 1: // incluir
		$query =  " INSERT INTO lojas ";
		$query .= " VALUES ( null, ";
		$query .= " $cidade, ";
		$query .= " '$nome', ";
		$query .= " '$local') ";
		
		mysql_query($query);
	
		$query = 	" 	select 	* ";
		$query .=	"	from  	lojas "; 
		$query .=	"	where nome = '$nome' "; 
		$query .=	"	and local = '$local' "; 
		$query .=	"	and cidade_id = $cidade";
		
		$resultSet = mysql_query($query);
		while($dados = mysql_fetch_array($resultSet)){
			$saida = $dados["loja_id"];
			$saida .= "\n";
			echo $saida;
		}
		break;
		
	case 2: // atualizar
		
		$query =  " UPDATE lojas ";
		$query .= "	  set nome='$nome', ";
		$query .= "   local = '$local', ";
		$query .= "   cidade_id = $cidade ";
		$query .= " WHERE loja_id = $loja_id";
		
		mysql_query($query);
		/*
		$query = 	" 	select 	* ";
		$query .= 	"	from  lojas ";
		$query .= 	"	where loja_id = $loja_id";
		
		$saida = "";
		$resultSet = mysql_query($query);
		while($dados = mysql_fetch_array($resultSet)){
			$saida = $dados["loja_id"];
			$saida .= $separador;
			$saida .= $dados["nome"];
			$saida .= $separador;
			$saida .= $dados["local"];
			$saida .= $separador;
			$saida .= $dados["cidade_id"];
			$saida .= "\n";
			echo $saida;
		}*/
			break;
	
	default:
		echo 'dados inconsistentes.';
		break;
}


?>

