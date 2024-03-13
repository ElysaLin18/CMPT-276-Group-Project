import pandas as pd


donor_id_path = "/PathTo/Donation_No_sheet.xlsx"
sample_path = "/PathTo/new-westminster-crc_2023_12-ministries_-_SAMPLE.xlsx"
output_path = "output.xlsx"

donor_id_df = pd.read_excel(donor_id_path, header=None)


name_id_map = pd.Series(donor_id_df[1].values, index=donor_id_df[0]).to_dict()

sample_df = pd.read_excel(sample_path, sheet_name=None)

first_sheet_df = sample_df[list(sample_df.keys())[0]]
second_sheet_df = pd.read_excel(sample_path, sheet_name=1, header=None)
third_sheet_df = pd.read_excel(sample_path, sheet_name=2, header=None)


columns_to_keep_indexes = [0, 9, 10, 11, 12, 14, 15, 16]


second_sheet_filtered_df = second_sheet_df.iloc[:, columns_to_keep_indexes].copy()
third_sheet_filtered_df = third_sheet_df.iloc[:, columns_to_keep_indexes].copy()


column_names = ["Name", "Date", "Donation", "Charge Back", "Total", "Envelope", "Payment Method", "Cause"]
second_sheet_filtered_df.columns = column_names
all_sheets = []
for sheet_number in range(2, 6):
    sheet_df = pd.read_excel(sample_path, sheet_name=sheet_number, header=None)
    sheet_filtered_df = sheet_df.iloc[:, columns_to_keep_indexes].copy()
    sheet_filtered_df.columns = column_names
    sheet_filtered_df['Name'] = sheet_filtered_df['Name'].apply(
        lambda name: f"{name_id_map[name]}" if name in name_id_map else name)
    sheet_filtered_df['Envelope'] = sheet_filtered_df['Name'].apply(
        lambda name: name.split('-')[-1] if '-' in name else ''
    )
    
    all_sheets.append(sheet_filtered_df)

combined_sheets_df = pd.concat(all_sheets, ignore_index=True)


second_sheet_filtered_df['Name'] = second_sheet_filtered_df['Name'].apply(
    lambda name: f"{name_id_map.get(name)}" if name in name_id_map else name
)
second_sheet_filtered_df['Envelope'] = second_sheet_filtered_df['Name'].apply(
    lambda name: name.split('-')[-1] if '-' in name else ''
)


with pd.ExcelWriter(output_path, engine='openpyxl') as writer:
    first_sheet_df.to_excel(writer, sheet_name='Totals', index=False)
    second_sheet_filtered_df.to_excel(writer, sheet_name='-1-  Sunday Giving- For New Wes', index=False)
    combined_sheets_df.to_excel(writer, sheet_name='-2nd cause - ALL', index=False)
    
print("Done")