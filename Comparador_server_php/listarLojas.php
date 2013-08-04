<?PHP
	
include_once 'connect.php';

$cidade = $_GET["cidade"];
$separador = ">";

if ($cidade) {
	$query = 	" 	select 	*";
	$query .= 	"	from  lojas ";
	$query .= 	"	where cidade_id = $cidade";
	
	$saida = "";
	$resultSet = mysql_query($query);
	while($dados = mysql_fetch_array($resultSet)){
		$saida .= $dados["loja_id"];
		$saida .= $separador;
		$saida .= $dados["nome"];
		$saida .= $separador;
		$saida .= $dados["local"];
		$saida .= "\n";
	} 
	echo $saida;
} else {
	echo 'dados inconsistentes.';		
}

?>

