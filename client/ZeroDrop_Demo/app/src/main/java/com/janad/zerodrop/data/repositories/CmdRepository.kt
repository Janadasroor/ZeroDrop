package com.janad.zerodrop.data.repositories

import com.janad.zerodrop.data.api.ApiService
import com.janad.zerodrop.data.api.ExecCmdReq
import com.janad.zerodrop.data.api.ExecCmdRes
import com.janad.zerodrop.data.api.ExecQueryReq
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class CmdRepository @Inject constructor(private val api: ApiService) {

    /**
     * Execute a server command
     * @param command the shell command to run
     * @return ExecCmdRes containing output or error
     */
    suspend fun executeCommand(command: String): Response<ExecCmdRes> {
        return api.executeCommand(ExecCmdReq(command))
    }


    /**
     * Execute a SQL query
     * @param query the SQL query string
     * @return ExecQueryRes containing rows or error
     */
    suspend fun executeQuery(query: String): Response<ResponseBody> {
        return api.executeQuery(ExecQueryReq(query))
    }

}