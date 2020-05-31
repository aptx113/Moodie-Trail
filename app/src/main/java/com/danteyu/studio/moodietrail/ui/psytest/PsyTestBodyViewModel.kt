package com.danteyu.studio.moodietrail.ui.psytest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danteyu.studio.moodietrail.MoodieTrailApplication
import com.danteyu.studio.moodietrail.R
import com.danteyu.studio.moodietrail.data.model.PsyTest
import com.danteyu.studio.moodietrail.data.Result
import com.danteyu.studio.moodietrail.repository.MoodieTrailRepository
import com.danteyu.studio.moodietrail.ui.login.UserManager
import com.danteyu.studio.moodietrail.network.LoadApiStatus
import com.danteyu.studio.moodietrail.util.Logger
import com.danteyu.studio.moodietrail.util.Util.getCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by George Yu on 2020/2/15.
 */
class PsyTestBodyViewModel(private val moodieTrailRepository: MoodieTrailRepository) : ViewModel() {

    val selectedInsomniaRadio = MutableLiveData<Int>()

    private val itemSleepScore: Float
        get() = when (selectedInsomniaRadio.value) {
            R.id.radio_insomnia_never -> NEVER
            R.id.radio_insomnia_seldom -> SELDOM
            R.id.radio_insomnia_sometimes -> SOMETIMES
            R.id.radio_insomnia_usually -> USUALLY
            R.id.radio_insomnia_always -> ALWAYS
            else -> NO_ANSWER
        }

    val selectedAnxietyRadio = MutableLiveData<Int>()

    private val itemAnxietyScore: Float
        get() = when (selectedAnxietyRadio.value) {
            R.id.radio_anxiety_never -> NEVER
            R.id.radio_anxiety_seldom -> SELDOM
            R.id.radio_anxiety_sometimes -> SOMETIMES
            R.id.radio_anxiety_usually -> USUALLY
            R.id.radio_anxiety_always -> ALWAYS
            else -> NO_ANSWER
        }

    val selectedAngerRadio = MutableLiveData<Int>()

    private val itemAngerScore: Float
        get() = when (selectedAngerRadio.value) {
            R.id.radio_angry_never -> NEVER
            R.id.radio_angry_seldom -> SELDOM
            R.id.radio_angry_sometimes -> SOMETIMES
            R.id.radio_angry_usually -> USUALLY
            R.id.radio_angry_always -> ALWAYS
            else -> NO_ANSWER
        }

    val selectedDepressionRadio = MutableLiveData<Int>()

    private val itemDepressionScore: Float
        get() = when (selectedDepressionRadio.value) {
            R.id.radio_depression_never -> NEVER
            R.id.radio_depression_seldom -> SELDOM
            R.id.radio_depression_sometimes -> SOMETIMES
            R.id.radio_depression_usually -> USUALLY
            R.id.radio_depression_always -> ALWAYS
            else -> NO_ANSWER
        }

    val selectedInferiorityRadio = MutableLiveData<Int>()

    private val itemInferiorityScore: Float
        get() = when (selectedInferiorityRadio.value) {
            R.id.radio_inferiority_never -> NEVER
            R.id.radio_inferiority_seldom -> SELDOM
            R.id.radio_inferiority_sometimes -> SOMETIMES
            R.id.radio_inferiority_usually -> USUALLY
            R.id.radio_inferiority_always -> ALWAYS
            else -> NO_ANSWER
        }

    val selectedSuicideRadio = MutableLiveData<Int>()

    private val itemSuicideScore: Float
        get() = when (selectedSuicideRadio.value) {
            R.id.radio_suicide_never -> NEVER
            R.id.radio_suicide_seldom -> SELDOM
            R.id.radio_suicide_sometimes -> SOMETIMES
            R.id.radio_suicide_usually -> USUALLY
            R.id.radio_suicide_always -> ALWAYS
            else -> NO_ANSWER
        }

    // Handle the error for submit
    private val _invalidSubmit = MutableLiveData<Int>()

    val invalidSubmit: LiveData<Int>
        get() = _invalidSubmit

    // Handle when write down is successful
    private val _submitSuccess = MutableLiveData<Boolean>()

    val submitSuccess: LiveData<Boolean>
        get() = _submitSuccess

    private val _navigateToPsyTestResult = MutableLiveData<PsyTest>()

    val navigateToPsyTestResult: LiveData<PsyTest>
        get() = _navigateToPsyTestResult

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        Logger.i("------------------------------------")
        Logger.i("[${this::class.simpleName}]${this}")
        Logger.i("------------------------------------")

    }

    fun prepareSubmit() {

        when {
            itemSleepScore == NO_ANSWER -> _invalidSubmit.value = INVALID_FORMAT_INSOMNIA_EMPTY
            itemAnxietyScore == NO_ANSWER -> _invalidSubmit.value = INVALID_FORMAT_ANXIETY_EMPTY
            itemAngerScore == NO_ANSWER -> _invalidSubmit.value = INVALID_FORMAT_ANGER_EMPTY
            itemDepressionScore == NO_ANSWER -> _invalidSubmit.value = INVALID_FORMAT_DEPRESSION_EMPTY
            itemInferiorityScore == NO_ANSWER -> _invalidSubmit.value = INVALID_FORMAT_INFERIORITY_EMPTY
            itemSuicideScore == NO_ANSWER -> _invalidSubmit.value = INVALID_FORMAT_SUICIDE_EMPTY

            else -> submit()
        }

    }

    private fun submit() {
        if (selectedInsomniaRadio.value == null || selectedAnxietyRadio.value == null
            || selectedAngerRadio.value == null || selectedDepressionRadio.value == null
            || selectedInferiorityRadio.value == null || selectedSuicideRadio.value == null
        ) return

        UserManager.id?.let {
            postPsyTest(
                it, PsyTest(
                    itemSleep = itemSleepScore,
                    itemAnxiety = itemAnxietyScore,
                    itemAnger = itemAngerScore,
                    itemDepression = itemDepressionScore,
                    itemInferiority = itemInferiorityScore,
                    itemSuicide = itemSuicideScore,
                    createdTime = getCalendar().timeInMillis
                )
            )
        }
    }

    private fun postPsyTest(uid: String, psyTest: PsyTest) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = moodieTrailRepository.postPsyTest(uid, psyTest)

            _submitSuccess.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    navigateToPsyTestResult(psyTest)
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    _invalidSubmit.value = POST_PSY_TEST_FAIL
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value =
                        MoodieTrailApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
        }
    }

    private fun navigateToPsyTestResult(psyTest: PsyTest) {
        _navigateToPsyTestResult.value = psyTest
    }

    fun onPsyTestResultNavigated() {
        _navigateToPsyTestResult.value = null
    }

    companion object {

        const val NO_ANSWER = -1f
        const val NEVER = 0f
        const val SELDOM = 1f
        const val SOMETIMES = 2f
        const val USUALLY = 3f
        const val ALWAYS = 4f

        const val INVALID_FORMAT_INSOMNIA_EMPTY = 0x20
        const val INVALID_FORMAT_ANXIETY_EMPTY = 0x21
        const val INVALID_FORMAT_ANGER_EMPTY = 0x22
        const val INVALID_FORMAT_DEPRESSION_EMPTY = 0x23
        const val INVALID_FORMAT_INFERIORITY_EMPTY = 0x24
        const val INVALID_FORMAT_SUICIDE_EMPTY = 0x25
        const val POST_PSY_TEST_FAIL = 0x26
    }
}