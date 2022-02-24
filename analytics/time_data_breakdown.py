import pandas as pd
import numpy as np

def count_high_teleop(high_list):
    # results = []
    # for times in high_list:
    #     timeList = [] if times is None else times.split(',')
    #     res = sum(1 if (int(x) >= 15) else 0 for x in timeList)
    #     results.append(res)
    # return results
        
    return [(sum(1 if (int(x) >= 15) else 0 for x in ([] if times is None else times.split(',')))) for times in high_list]

def count_low_teleop(low_list):
    results = []
    for times in low_list:
        timeList = [] if times is None else times.split(',')
        res = sum(1 if (int(x) >= 15) else 0 for x in timeList)
        results.append(res)
    return results

def count_acquires_teleop(acquires_list):
    results = []
    for acquires in acquires_list:
        acquireList = [] if acquires is None else acquires.split(',')
        res = sum(1 if (int(x) >= 15) else 0 for x in acquireList)
        results.append(res)
    return results

def count_missed_teleop(missed_list):
    results = []
    for times in missed_list:
        timeList = [] if times is None else times.split(',')
        res = sum(1 if (int(x) >= 15) else 0 for x in timeList)
        results.append(res)
    return results

def count_high_auto(high_list):
    results = []
    for times in high_list:
        timeList = [] if times is None else times.split(',')
        res = sum(1 if (int(x) < 15) else 0 for x in timeList)
        results.append(res)
    return results

def count_low_auto(low_list):
    results = []
    for times in low_list:
        timeList = [] if times is None else times.split(',')
        res = sum(1 if (int(x) < 15) else 0 for x in timeList)
        results.append(res)
    return results

def count_acquires_auto(acquires_list):
    results = []
    for acquires in acquires_list:
        acquireList = [] if acquires is None else acquires.split(',')
        res = sum(1 if (int(x) < 15) else 0 for x in acquireList)
        results.append(res)
    return results

def count_missed_auto(missed_list):
    results = []
    for times in missed_list:
        timeList = [] if times is None else times.split(',')
        res = sum(1 if (int(x) < 15) else 0 for x in timeList)
        results.append(res)
    return results

def average_cycle_time(acquires_list, low_list, high_list):
    result = []
    for (acq, low, high) in zip(acquires_list, low_list, high_list):
        acquires = [] if acq is None else acq.split(",")
        scores = ([] if low is None else low.split(",")) + ([] if high is None else high.split(","))

        acquires = sorted([int(x) for x in acquires])
        scores = sorted([int(x) for x in scores])

        times = []

        # Loop through acquires and scores
        i, j, oldest, oldestIndex, current = 0, 0, 0, 0, 0
        while j < len(scores):
            if (i < len(acquires) and acquires[i] < scores[j]):
                if (oldest == 0):
                    oldest = acquires[i]
                    oldestIndex = i
                current += 1
                # print("Acquired at " + str(acquires[i]) + " " + str(i))
                # print("Current: " + str(current) + "\n")
                i += 1
            else:
                current -= 1
                # print("Released at " + str(scores[j]) + " " + str(j))
                # print("Current: " + str(current) + "\n")
                j += 1
                if (current == 0):
                    # print("Cycle time: " + str(scores[j - 1] - oldest) + "\n")
                    # print(str(j - oldestIndex))
                    if (j - oldestIndex == 1):
                        times.append((scores[j - 1] - oldest) * 2)
                    else:
                        times.append(scores[j - 1] - oldest)
                    oldest = 0
                    oldestIndex = 0

        result.append(0 if len(times) == 0 else (sum(times) / len(times)))
    return result

def calculate_accuracy_teleop(count_acquires_list, count_missed_list):
    results = []
    for (acquires, missed) in zip(count_acquires_list, count_missed_list):
        results.append(((acquires - missed) / acquires) if (acquires > 0) else 0)
    return results

def calculate_climb_time(climb_start_list, climb_end_list):
    results = []
    for (start, end) in zip(climb_start_list, climb_end_list):
        results.append(end - start)
    return results

def calculate_climb_points(climb_result_list):
    results = []
    for climb in climb_result_list:
        if climb == 'T':
            results.append(15)
        elif climb == 'H':
            results.append(10)
        elif climb == 'M':
            results.append(6)
        elif climb == 'L':
            results.append(4)
        elif climb == '0':
            results.append(0)
    return results

def prep_fields(input_df):

    df = pd.DataFrame ({
        'Match' : input_df['Match'],
        'Team' : input_df['Team'],
        'Is Red' : input_df['Is Red'],
        'Acquired Tele' : count_acquires_teleop(input_df['Acquire']),
        'Acquired Auto' : count_acquires_auto(input_df['Acquire']),
        'Scored Low Tele' : count_low_teleop(input_df['Score Low']),
        'Scored High Tele' : count_high_teleop(input_df['Score High']),
        'Scored Low Auto' : count_low_auto(input_df['Score Low']),
        'Scored High Auto' : count_high_auto(input_df['Score High']),
        'Missed': count_missed_teleop(input_df['Miss']),
        'Climb Start': input_df['Climb Start'],
        'Climb End': input_df['Climb End'],
        'Climb Time': calculate_climb_time(input_df['Climb Start'], input_df['Climb End']),
        'Climb Points': calculate_climb_points(input_df['Final Position']),
        'Cycle Time': average_cycle_time(input_df['Acquire'], input_df['Score Low'], input_df['Score High'])
        })

    df = pd.DataFrame ({
        'Match' : df['Match'],
        'Team' : df['Team'],
        'Is Red' : df['Is Red'],
        'Acquired Tele' : df['Acquired Tele'],
        'Acquired Auto' : df['Acquired Auto'],
        'Scored Low Tele' : df['Scored Low Tele'],
        'Scored High Tele' : df['Scored High Tele'],
        'Scored Low Auto' : df['Scored Low Auto'],
        'Scored High Auto' : df['Scored High Auto'],
        'Missed': df['Missed'],
        'Climb Start': df['Climb Start'],
        'Climb End': df['Climb End'],
        'Climb Time': df['Climb Time'],
        'Climb Points': df['Climb Points'],
        'Accuracy': calculate_accuracy_teleop(df['Acquired Tele'], df['Missed']),
        'Cycle Time': df['Cycle Time']
    })

    return df

    
def get_output_schema():
    return pd.DataFrame ({
        'Match': prep_int(),
        'Team': prep_int(),
        'Is Red': prep_bool(),
        'Acquired Tele': prep_int(),
        'Acquired Auto': prep_int(),
        'Scored Low Tele': prep_int(),
        'Scored High Tele': prep_int(),
        'Scored Low Auto': prep_int(),
        'Scored High Auto': prep_int(),
        'Missed': prep_int(),
        'Climb Start': prep_int(),
        'Climb End': prep_int(),
        'Climb Time': prep_int(),
        'Climb Points': prep_int(),
        'Accuracy': prep_decimal(),
        'Cycle Time': prep_decimal(),
    })
