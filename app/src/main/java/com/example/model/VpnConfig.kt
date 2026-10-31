package com.example.model

import java.util.UUID

data class VpnConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Default Profile",
    // [mode]
    val connectionMode: Int = 1, // 0: SSH Direct, 1: Payload, 2: SSL/TLS (SNI), 3: Payload + SNI
    val autoReplace: Boolean = true, // 'y' or 'n'
    // [Payload]
    val payload: String = "GET / HTTP/1.1[crlf]Host: [host][crlf]Connection: Upgrade[crlf]User-Agent: [ua][crlf]Upgrade: websocket[crlf][crlf]",
    val proxyIp: String = "",
    val proxyPort: String = "",
    // [ssh]
    val sshHost: String = "",
    val sshPort: String = "22",
    val sshUsername: String = "",
    val sshPassword: String = "",
    val enableCompression: Boolean = false, // 'y' or 'n'
    val authMethod: String = "password", // 'password' or 'publickey'
    val publicKey: String = "",
    // [sni]
    val sniServerName: String = "",
    val updatedAt: Long = System.currentTimeMillis()
) {

    /**
     * Converts configuration into standard settings.ini format.
     */
    fun toIniString(): String {
        val sb = StringBuilder()
        sb.append("[mode]\n")
        sb.append("connection_mode = $connectionMode\n")
        sb.append("auto_replace = ${if (autoReplace) "y" else "n"}\n\n")

        sb.append("[Payload]\n")
        sb.append("payload = $payload\n")
        sb.append("proxyip = $proxyIp\n")
        sb.append("proxyport = $proxyPort\n\n")

        sb.append("[ssh]\n")
        sb.append("host = $sshHost\n")
        sb.append("port = $sshPort\n")
        sb.append("username = $sshUsername\n")
        sb.append("password = $sshPassword\n")
        sb.append("enable_compression = ${if (enableCompression) "y" else "n"}\n")
        sb.append("#authentication method : password | publickey\n")
        sb.append("auth_methode = $authMethod\n\n")

        sb.append("[sni]\n")
        sb.append("server_name = $sniServerName\n")

        if (authMethod == "publickey" && publicKey.isNotBlank()) {
            sb.append("\n# [publickey]\n# $publicKey\n")
        }
        return sb.toString()
    }

    /**
     * Replicates the exact Python inject.py payload formatting.
     */
    fun getFormattedPayload(): String {
        val host = if (sshHost.isNotBlank()) sshHost else "127.0.0.1"
        val port = if (sshPort.isNotBlank()) sshPort else "22"
        var formatted = payload

        formatted = formatted.replace("[crlf]", "\r\n")
        formatted = formatted.replace("[crlf*2]", "\r\n\r\n")
        formatted = formatted.replace("[cr]", "\r")
        formatted = formatted.replace("[lf]", "\n")
        formatted = formatted.replace("\\r", "\r")
        formatted = formatted.replace("\\n", "\n")
        formatted = formatted.replace("[protocol]", "HTTP/1.0")
        formatted = formatted.replace("[ua]", "Dalvik/2.1.0")
        formatted = formatted.replace("[raw]", "CONNECT $host:$port HTTP/1.0\r\n\r\n")
        formatted = formatted.replace("[real_raw]", "CONNECT $host:$port HTTP/1.0\r\n\r\n")
        formatted = formatted.replace("[netData]", "CONNECT $host:$port HTTP/1.0")
        formatted = formatted.replace("[realData]", "CONNECT $host:$port HTTP/1.0")
        formatted = formatted.replace("[split_delay]", "[delay_split]")
        formatted = formatted.replace("[split_instant]", "[instant_split]")
        formatted = formatted.replace("[method]", "CONNECT")
        formatted = formatted.replace("[ssh]", "$host:$port")
        formatted = formatted.replace("[lfcr]", "\n\r")
        formatted = formatted.replace("[host_port]", "$host:$port")
        formatted = formatted.replace("[host]", host)
        formatted = formatted.replace("[port]", port)
        formatted = formatted.replace("[auth]", "")
        formatted = formatted.replace("[split]", "=0.1=")
        formatted = formatted.replace("[delay_split]", "=0.5=")
        formatted = formatted.replace("[instant_split]", "=0.0=")

        return formatted
    }

    companion object {
        fun createDefault(): VpnConfig {
            return VpnConfig(
                name = "ค่าเริ่มต้น (WebSocket CDN)",
                connectionMode = 1,
                autoReplace = true,
                payload = "GET / HTTP/1.1[crlf]Host: [host][crlf]Connection: Upgrade[crlf]User-Agent: [ua][crlf]Upgrade: websocket[crlf][crlf]",
                proxyIp = "",
                proxyPort = "8080",
                sshHost = "sg-server.net",
                sshPort = "22",
                sshUsername = "vpnuser",
                sshPassword = "vpnpassword",
                enableCompression = false,
                authMethod = "password",
                sniServerName = ""
            )
        }

        fun createSshDirect(): VpnConfig {
            return VpnConfig(
                name = "โหมด 0: SSH Direct",
                connectionMode = 0,
                autoReplace = true,
                payload = "CONNECT [host_port] HTTP/1.1[crlf][crlf]",
                proxyIp = "",
                proxyPort = "",
                sshHost = "ssh.mycloud.com",
                sshPort = "22",
                sshUsername = "root",
                sshPassword = "",
                enableCompression = true,
                authMethod = "password",
                sniServerName = ""
            )
        }

        fun createSslSni(): VpnConfig {
            return VpnConfig(
                name = "โหมด 2: SSL/TLS (SNI)",
                connectionMode = 2,
                autoReplace = true,
                payload = "CONNECT [host_port] HTTP/1.1[crlf]Host: [host][crlf][crlf]",
                proxyIp = "104.16.249.249",
                proxyPort = "443",
                sshHost = "vpn.cloudflare-cdn.com",
                sshPort = "443",
                sshUsername = "vpnuser",
                sshPassword = "",
                enableCompression = false,
                authMethod = "password",
                sniServerName = "fast.com"
            )
        }

        fun createPayloadSni(): VpnConfig {
            return VpnConfig(
                name = "โหมด 3: Payload + SNI",
                connectionMode = 3,
                autoReplace = true,
                payload = "GET / HTTP/1.1[crlf]Host: [host][crlf]Upgrade: websocket[crlf]Connection: Upgrade[crlf][crlf]",
                proxyIp = "",
                proxyPort = "443",
                sshHost = "tunnel.vipserver.net",
                sshPort = "443",
                sshUsername = "client01",
                sshPassword = "",
                enableCompression = false,
                authMethod = "password",
                sniServerName = "zoom.us"
            )
        }

        /**
         * Parses a standard settings.ini text into a VpnConfig object.
         */
        fun parseIni(iniText: String, profileName: String = "นำเข้าจากการตั้งค่า"): VpnConfig {
            var currentSection = ""
            var mode = 1
            var autoReplace = true
            var payload = ""
            var proxyIp = ""
            var proxyPort = ""
            var sshHost = ""
            var sshPort = "22"
            var sshUsername = ""
            var sshPassword = ""
            var compression = false
            var authMethod = "password"
            var sni = ""

            for (rawLine in iniText.lines()) {
                val line = rawLine.trim()
                if (line.isEmpty() || line.startsWith("#") || line.startsWith(";")) {
                    continue
                }
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.substring(1, line.length - 1).lowercase()
                    continue
                }
                val eqIdx = line.indexOf('=')
                if (eqIdx == -1) continue

                val key = line.substring(0, eqIdx).trim().lowercase()
                val value = line.substring(eqIdx + 1).trim()

                when (currentSection) {
                    "mode" -> {
                        when (key) {
                            "connection_mode" -> mode = value.toIntOrNull() ?: 1
                            "auto_replace" -> autoReplace = value.equals("y", ignoreCase = true)
                        }
                    }
                    "payload" -> {
                        when (key) {
                            "payload" -> payload = value
                            "proxyip" -> proxyIp = value
                            "proxyport" -> proxyPort = value
                        }
                    }
                    "ssh" -> {
                        when (key) {
                            "host" -> sshHost = value
                            "port" -> sshPort = value
                            "username" -> sshUsername = value
                            "password" -> sshPassword = value
                            "enable_compression" -> compression = value.equals("y", ignoreCase = true)
                            "auth_methode" -> authMethod = if (value.equals("publickey", ignoreCase = true)) "publickey" else "password"
                        }
                    }
                    "sni" -> {
                        when (key) {
                            "server_name" -> sni = value
                        }
                    }
                }
            }

            if (payload.isBlank()) {
                payload = "GET / HTTP/1.1[crlf]Host: [host][crlf]Connection: Upgrade[crlf]User-Agent: [ua][crlf]Upgrade: websocket[crlf][crlf]"
            }

            return VpnConfig(
                name = profileName,
                connectionMode = mode,
                autoReplace = autoReplace,
                payload = payload,
                proxyIp = proxyIp,
                proxyPort = proxyPort,
                sshHost = sshHost,
                sshPort = sshPort,
                sshUsername = sshUsername,
                sshPassword = sshPassword,
                enableCompression = compression,
                authMethod = authMethod,
                sniServerName = sni
            )
        }
    }
}
