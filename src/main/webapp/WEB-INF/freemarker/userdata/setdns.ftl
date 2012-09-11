<powershell>
$NICs = Get-WMIObject Win32_NetworkAdapterConfiguration `
| where{$_.IPEnabled -eq "TRUE"}
foreach($NIC in $NICs) {
$DNSServers = "${PRIMARY_DNS}","${SECONDARY_DNS}"
$NIC.SetDNSServerSearchOrder($DNSServers)
$NIC.SetDynamicDNSRegistration("FALSE")
}
</powershell>